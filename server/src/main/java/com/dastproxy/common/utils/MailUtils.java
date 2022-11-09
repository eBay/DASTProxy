/**
 * This is the utility class for sending mails.
 *
 * @author Kiran Shirali (kshirali@ebay.com)
 * 
 * Modified on July 2014 by:
 * 
 * @author Rajvi Shah (rajvshah@paypal.com)
 */
package com.dastproxy.common.utils;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.apache.velocity.spring.VelocityEngineUtils;
import com.dastproxy.common.constants.AppScanConstants;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MailUtils {

	// Logger object for this class
	private static final Logger LOGGER = LogManager.getLogger(MailUtils.class
			.getName());

	private final static JavaMailSender javaMailSender = new JavaMailSenderImpl();

	// Velocity is a template engine for e-mails.
	// This allows us to send e-mails with pre defined templates.
	private final static VelocityEngine velocityEngine = new VelocityEngine();

	// Send Email when scan starts running
	public static void sendEmail(final String toEmailAddresses, final String ccEmailAddresses,
			final String fromEmailAddress, final String subject,
			final Map<String, Object> model, final String template) {

		setProperties();
		prepareEmail(toEmailAddresses, ccEmailAddresses, fromEmailAddress, subject, model,
				template);
	}

	// Set Properties for velocityEngine and mailSender
	private static void setProperties() {
		// Properties for loading resource
		velocityEngine.setProperty("resource.loader", "class");
		velocityEngine
				.setProperty("class.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.setProperty("class.resource.loader.path",
				"/WEB-INF/classes");

		// Set hostName
		((JavaMailSenderImpl) javaMailSender)
				.setHost(AppScanConstants.SMTP_SERVER);
	}

	// Create email
	private static void prepareEmail(final String toEmailAddresses, final String ccEmailAddresses,
			final String fromEmailAddress, final String subject,
			final Map<String, Object> model, final String template) {
		// TODO send email with attachment of the report.
		// final String attachmentPath, final String attachmentName) {

		LOGGER.debug(AppScanConstants.DEBUG_MSG_IN_SIMPLE_SEND_MAIL_FUNCTION);

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			public void prepare(final MimeMessage mimeMessage)
					throws MessagingException {

				final MimeMessageHelper message = new MimeMessageHelper(
						mimeMessage, true);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(AppScanConstants.DEBUG_MSG_MAIL_FROM_ADDRESS_SET
							+ toEmailAddresses);
					LOGGER.debug(AppScanConstants.DEBUG_MSG_MAIL_FROM_ADDRESS_SET
							+ fromEmailAddress);
					LOGGER.debug("The cc address to send mail is: "+ ccEmailAddresses);
					
				}

				message.setTo(toEmailAddresses);
				message.setFrom(fromEmailAddress);
				message.setSubject(subject);
				if (ccEmailAddresses!=null)message.setCc(ccEmailAddresses); 
					
				// set desired template as body for email
				final String body = VelocityEngineUtils
						.mergeTemplateIntoString(velocityEngine, template,
								"UTF-8", model);
				message.setText(body, true);

			}
		};
		getJavaMailSender().send(preparator);
		LOGGER.debug(AppScanConstants.DEBUG_MSG_MAIL_HAS_BEEN_SENT);
	}

	/**
	 * Getter for velocity Engine
	 * 
	 * @return velocityEngine
	 */
	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	// NOTE: Because we are directly instantiating the object at Class variable
	// level,
	// this setter is not required.
	// However, keeping this here so that we can change the creation to
	// Dependency Injection at some later date.
	// TODO: Implement Dependency Injection
	/*
	 * public void setVelocityEngine(VelocityEngine velocityEngine) {
	 * MailUtils.velocityEngine = velocityEngine; }
	 */

	/**
	 * Getter for JavaMail Sender 
	 * @return javaMailSender
	 */
	public static JavaMailSender getJavaMailSender() {
		return javaMailSender;
	}

	// NOTE: Because we are directly instantiating the object at Class variable
	// level, this setter is not required.
	// However, keeping this here so that we can change the creation to
	// Dependency Injection at some later date.
	// TODO: Implement Dependency Injection
	/*
	 * public static void setJavaMailSender(JavaMailSender javaMailSender) {
	 * MailUtils.javaMailSender = javaMailSender; }
	 */

}