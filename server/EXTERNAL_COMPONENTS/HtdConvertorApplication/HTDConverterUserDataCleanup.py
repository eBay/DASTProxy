#!/usr/bin/python
'''
* On script execution, script will recursively crawl the file system
* beginning in C:\HARtoHTDConverter\Users directory, looking for any .HAR
* or .HTD files that have a create date that is older than the specified
* number of days. Since these resources will not longer be needed by the
* DASTProxy application we will delete any file identified met by the 
* conditions above.

* Version Update from 0.1 to 0.2:
* - Added support to ignore developers directories to not delete their recordings.

* - Added email enhancement on script completion to include Total Number of Files Discovered
* - and Total Number of Files Deleted.

* - Added New Time Format to Email Notifications. WAS: yr-mon-day. NOW: mon-day-yr

NEXT:  Along with sending metrics via email, we would also want to house the metrics in a DB.

NEXT: Query PD DL from DLMANAGER, check users in said DL, and ignore any user file in root that
was found in the DL from DLMANAGER.

'''

__author__ = "Brett Bergin"
__publish_date__ = "July 19th, 2014"
__version__ = "v0.2"


''' SET LOGGING LEVEL HERE '''
log_handler = 'INFO'
# log_handler = 'DEBUG'

''' SET EMAIL EVENT HANDLER (To Receive Email Notifications or Not) '''
email_events = True
# email_events = False

''' Sets Up Script Logging To Log To 'logs' Dir In Tomcat Dir With Rest Of DASTProxy Log Files. '''
import logging, time
log_time = time.strftime("%Y-%m-%d")
log = logging.getLogger('ResourceCleanup')
handler = logging.FileHandler('C:\\Program Files\\apache-tomcat-8.0.20\\logs\\ResourceCleanup.log.%s.txt' % (log_time))
formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
handler.setFormatter(formatter)
log.addHandler(handler)

''' Import Needed Python Libs '''
import sys
try:
	import os, datetime
	import MimeWriter, StringIO, smtplib
except Exception, err:
	log.error('[-] Cannot Import Needed Python Libs. [EXITING WITH ERROR]: %s' % (err))
	sys.exit()

def main():
	''' Script Globals '''
	current_time = time.strftime('%m-%d-%Y At: %I:%M:%S.') # Gets Current System Time
	crawl_root = "C:\\HARtoHTDConvertor\\Users" # Start Walking The File System From This Dir For Files With HAR/HTD Extensions.
	file_types = ['.har','.htd'] # File Extensions To Parse For
	days_back_to_delete = 7 # Delete Any Files Older Than This Many Days.
	
	# Checks for log level
	if log_handler.lower() == 'info':
		log.setLevel(logging.INFO)
		log.info('[+] Cleanup Script Started On: %s' % (current_time))
		log.info('[+] Script Log Level: %s.' % (log_handler))
		if email_events == True:
			log.info('[+] Script Sending Script Start Email Notification: %s.' % str(email_events))
		elif email_events == False:
			log.info('[+] Script Sending Script Start Email Notification: %s.' % str(email_events))
	elif log_handler.lower() == 'debug':
		log.setLevel(logging.DEBUG)
		log.info('[+] Cleanup Script Started On: %s' % (current_time))
		log.info('[+] Script Log Level: %s.' % (log_handler))
		if email_events == True:
			log.info('[+] Script Sending Email Notifications: %s.' % str(email_events))
		elif email_events == False:
			log.info('[+] Script Sending Email Notifications: %s.' % str(email_events))
	
	''' Script Start '''
	found_files = get_all_files(crawl_root, file_types)
	files_to_delete = find_all_files_out_of_date(found_files, days_back_to_delete)
	delete_out_of_date_files(files_to_delete)
	''' Script End '''
	
	''' Email Alert Handler '''
	if email_events == True:
		send_success = send_success_email(found_files,files_to_delete)
		if send_success == True:
			log.info("[+] Cleanup Script Complete.")
			return
		elif send_success == False:
			log.error('[-] Could Not Send Email Notification Of Successful Script Completion.')
			log.error('[-] Script Did Not Complete Successfully.')
			send_failure_email('Success-Email Send Failure.')
			return
	elif email_events == False:
		log.info("[+] Cleanup Script Complete.")
		return
	return
	
def get_all_files(root, types):
	collected_files = [] #Container for found HAR/HTD file.
	try:
		log.info("[+] Crawling %s for all HAR/HTD files." % (root))
		for paths, dirs, files in os.walk(root):
			# Ignores Developers Directories, to not delete their recordings.
			if not paths.endswith('kshirali') and not paths.endswith('rajvshah'):
				for file in files:		
				# Only gets files with .har or .htd file extensions.
					if file.endswith(types[0]) or file.endswith(types[1]):				
						found_file = os.path.join("%s\\%s" % (paths,file))
						# Appends file to list.
						collected_files.append(found_file) # File with if condition met, added to list.		
		log.info('[+] HAR/HTD files found: %d' % (len(collected_files)))
		# return the list of found HAR/HTDs
		return collected_files	
	except Exception, err:
		mesg = '[-] Cannot Collect All HTD/HAR Files in Users Path. [EXITING WITH ERROR]: %s' % (err)
		if email_events == True:
			log.error(mesg)
			send_failure_email(mesg)
		elif email_events == False:
			log.error(mesg)
		sys.exit()
		
def find_all_files_out_of_date(files, dayz):
	try:
		log.info("[+] Performing Time Delta Analysis On Files Older Than %d Days." % (dayz))
		now = datetime.datetime.now() # Current_Datetime
		keep_files, del_files, failed_files = [],[],[] # List objects to use as containers for parsed time deltas
		for file in files: # Loops over all HAR/HTDs Found In Crawl.	
			try:
				# Parses Time Attributes From Timestamp In Filename.
				file_day = int(file.split('Recordings-')[1].split('-')[1])
				file_month = int(file.split('Recordings-')[1].split('-')[2])
				file_year = int(file.split('Recordings-')[1].split('-')[-1].split('_')[0])
				file_hour = int(file.split('Recordings-')[1].split('-')[3].split('_')[1].split('.')[0])
				file_min = int(file.split('Recordings-')[1].split('-')[3].split('_')[1].split('.')[1])
				
				# Performs Time Delta Analysis On The Difference Between Script Run Time & File Creation Timestamp in Filename.
				file_date = datetime.datetime(file_year,file_month,file_day,file_hour,file_min)
				time_delta = datetime.timedelta(days=dayz)
				time_diff = now - file_date
				
				# Adds All Files That We Want To Delete to the 'del_files' list.
				if time_diff >= time_delta:
					del_files.append(file)
				# Adds All Files That We DONT Want To Delete to the 'keep_files' list. (For Debugging Purposes Only.)
				elif time_diff <= time_delta:
					keep_files.append(file)
			except:
				# Exception Handler To Catch Files That Didnt Match Parsing Criteria above. (For Debugging Purposes Only.)
				failed_files.append(file)
				
		total_success_parsed_files = len(keep_files) + len(del_files)
		log.debug("[+] Total Successfully Parsed Files: %d" % (total_success_parsed_files))
		
		# Adds any Failed Parsed Files To Log If DEBUG Flag Is Set.
		log.debug("[!] Total Failed Parsed Files: %d." % (len(failed_files)))
		
		for fail in failed_files:
			log.debug("[!] Parser Error On File: %s" % (fail))
		
		# Adds number of files to keep if DEBUG Flag Is Set.
		log.debug("[!] Total Files To Keep: %d" % (len(keep_files)))
		
		log.info("[+] Files To Be Deleted: %d" % (len(del_files)))
		log.info("[+] Time Delta Analysis On Create-Date/Current-Date Complete.")
		
		# Returns The Files We Want To Delete.
		return del_files	
	except Exception, err:
		mesg = '[-] Cannot Complete Time Delta Analysis On Existing HAR/HTD Files Found. [EXITING WITH ERROR]: %s' % (err)
		if email_events == True:
			log.error(mesg)
			send_failure_email(mesg)
		elif email_events == False:
			log.error(mesg)
		sys.exit()

def delete_out_of_date_files(old_files):
	try:
		log.info("[+] Deleting Out Of Date Files.")
		for old_file in old_files:	
			# Check if file really has a valid file path.
			if os.path.exists(old_file): 
				log.debug("[!] Attempting To Delete File: %s" % (old_file))	
				try:
					# This removes the old_file
					os.remove(old_file) 
				except Exception, err:
					log.error("[-] [ERROR]: %s | FOR FILE: %s" % (err, old_file))			
			else:
				log.debug("[!] File Returned Invalid File Path: %s" % (old_file))	
		log.info("[+] Deletion Complete.")
		return		
	except Exception, err:
		mesg = '[-] Cannot Perform Deletion Of All Out Of Date HAR/HTD Files. [EXITING WITH [ERROR]: %s' % (err)
		if email_events == True:
			log.error(mesg)
			send_failure_email(mesg)
		elif email_events == False:
			log.error(mesg)
		sys.exit()

def send_failure_email(fail_error):
	email_addr = '<INSER_EMAIL_TO_BE_ALERTED>'
	try:
		log.info("[+] Sending Failure Event Email Notification.")
		message = StringIO.StringIO()
		writer = MimeWriter.MimeWriter(message)
		writer.addheader('Subject', '[SCRIPT FAILURE]: User HAR/HTD Cleanup Script Failed.')
		writer.startmultipartbody('mixed')
		part = writer.nextpart()
		body, now = part.startbody('text/plain'), time.strftime('%m-%d-%Y At: %H:%M:%S')
		body.write('SCRIPT FAILURE ALERT:\n\nALERT TIME: %s\n\nERROR MESSAGE: \n\n%s' % (now, fail_error))
		writer.lastpart()
		try:
			log.info('[+] Sending Script Completion Email Notification.')
			smtp = smtplib.SMTP('<INSERT_SMTP_SERVER_ADDRESS>')
			smtp.sendmail(email_addr, email_addr, message.getvalue())
			smtp.quit()
			log.info('[+] Email Notification Successfully Sent.')
			return
		except Exception, err:
			log.error('[-] Could Not Send FAILURE Notification Email, [ERROR]: %s' % (err))
			return
	except Exception, err:
		log.error('[-] Could Not Prepare To Send FAILURE Email Notification, [ERROR]: %s' % (err))
		return

def send_success_email(all_files, files_to_delete):
	email_addr = '<INSER_EMAIL_TO_BE_ALERTED>'
	try:
		message = StringIO.StringIO()
		writer = MimeWriter.MimeWriter(message)
		writer.addheader('Subject', '[SCRIPT Success]: User HAR/HTD Cleanup Script Has Finished.')
		writer.startmultipartbody('mixed')
		part = writer.nextpart()
		body, now = part.startbody('text/plain'), time.strftime('%m-%d-%Y %H:%M:%S')
		body.write('HAR/HTD User File Cleanup Script Has Finished.\n\nRun Time: %s\n\n' % (now))
		body.write('Total Files Found: %s\n' % (str(len(all_files))))
		body.write('Total Files Deleted: %s\n' % (str(len(files_to_delete))))
		writer.lastpart()
		try:
			log.info('[+] Sending Email Notification.')
			smtp = smtplib.SMTP('<INSERT_SMTP_SERVER_ADDRESS>')
			smtp.sendmail(email_addr, email_addr, message.getvalue())
			smtp.quit()
			log.info('[+] Email Notification Successfully Sent.')
			return True
		except Exception, err:
			log.error('[-] Could Not Send Notification Email, [ERROR]: %s' % (err))
			return False
	except Exception, err:
		log.error('[-] Could Not Prepare To Send Email Notification, [ERROR]: %s' % (err))
		return False

if __name__ == '__main__':
	main()

