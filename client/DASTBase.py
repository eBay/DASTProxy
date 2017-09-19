import requests
import json
import ast
import traceback
from requests.auth import HTTPBasicAuth
from selenium import webdriver
from dast_config import user as user
from dast_config import password as password
from dast_config import dast_endpoint as endpoint


class DASTBase:
    # Base url and headers
    dast_proxy_base_url = r'{endpoint}/DASTProxy/rest/v1/{{}}'.format(endpoint=endpoint)
    start_dast_payload = {
        "proxy": None,
        "proxyIdentifier": "testuser@domain.com",
        "testCaseSuiteName":"AutoTestSuit",
        "testCaseName": "AutoTestCase",
        "testCaseClassName": "ClassName",
        "testCasePackageName": "com.test",
        "user": {
                "userId":"testuser"
            },
        "scanConfigurationParameters": [
            "Parameter1",
            "Parameter2",
        ]
     }

    stop_scan_payload = scan_req = {
        'proxy': {
            'proxyAddress':'10.123.123.123',
            'proxyPort':'1233',
            'newlyCreated':True
        },
        'proxyIdentifier':'testuser@domain.com',
        'testCaseSuiteName':'AutoTestSuit',
        'testCaseTagName':'AutoTest',
        'testCasePackageName':'com.test',
        'testCaseClassName':'ClassName',
        'testCaseName':'AutoTestCase',
        'user': {
            'userId':'testuser'
        },
        'scanConfigurationParameters':
            [
                'START_SCAN_AUTOMATIC'
            ]
    }
    headers = {'Content-type': 'application/json'}

    @staticmethod
    def stop_proxy(func_name, proxy, port):
        scan_payload = DASTBase.stop_scan_payload
        scan_payload['proxy'] = {'proxyAddress': proxy,
                                 'proxyPort': port,
                                 'newlyCreated': True}
        scan_payload['user']['userId'] = user
        scan_payload['proxyIdentifier'] = '%s@domain.com' % user
        scan_payload['testCaseName'] = func_name
        print DASTBase.dast_proxy_base_url.format('selenium/dastscan')
        s = json.dumps(scan_payload)
        scan_payload = json.loads(s)
        print scan_payload
        # str_payload =json.loads(scan_payload)
        # print str_payload
        response = requests.post(DASTBase.dast_proxy_base_url.format('selenium/dastscan'), json=scan_payload,
                                 auth=HTTPBasicAuth(user, password),
                                 headers=DASTBase.headers, verify=False)
        print "Response code: {}.".format(response.status_code)

    @staticmethod
    def cancel_recording():
        sub_url = r'security/{}/none/undefined'.format(user)
        print "Stopping the DAST scan..."
        response = requests.get(DASTBase.dast_proxy_base_url.format(sub_url), headers=DASTBase.headers, auth=HTTPBasicAuth(user, password), verify=False)
        data = response.json()
        if data['data'] != 'success':
            print 'Proxy is not stopped successfully.'

    @staticmethod
    def run_dast_scan(func):
        import urllib3
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

        # Once testing is done, stop the proxy
        sub_url = r'proxy/{}'.format(user)
        response = requests.get(DASTBase.dast_proxy_base_url.format(sub_url), headers=DASTBase.headers, verify=False)
        data = response.json()
        print data
        if data['data']:
            print 'One DAST scan is still in progress, will stop it first.'
            DASTBase.cancel_recording()
        try:
            # Start the recording on
            payload = DASTBase.start_dast_payload
            payload['user']['userId'] = user
            payload['proxyIdentifier'] = '%s@domain.com' % user
            payload['testCaseName'] = unicode(func.__name__)
            print DASTBase.dast_proxy_base_url.format('proxyui')
            s = json.dumps(payload)
            payload = json.loads(s)
            print payload
            response = requests.post(DASTBase.dast_proxy_base_url.format('proxy'), data=json.dumps(payload),
                                     auth=HTTPBasicAuth(user, password),
                                     headers=DASTBase.headers, verify=False)
            print "Response code: {}.".format(response.status_code)
            if response.status_code != 200:
                print 'ERROR happened in POST request'
                exit(0)
            data = response.json()
            print data
            proxy = data['data']['proxy']['proxyAddress']
            port = data['data']['proxy']['proxyPort']
            print 'get proxy {}:{}'.format(proxy, port)
            # Build selenium capability using above proxy and port
            profile = DASTBase.build_proxy(proxy, port)
            func(profile=profile)
        except:
            print traceback.format_exc()
        finally:
            # Once testing is done, stop the proxy
            DASTBase.stop_proxy(func.__name__, proxy, port)

    @staticmethod
    def build_proxy(proxy, port):
        profile = webdriver.FirefoxProfile()
        if proxy is not None and port is not None:
            profile.set_preference("network.proxy.type", 1)
            profile.set_preference("network.proxy.http", proxy)
            profile.set_preference("network.proxy.http_port", port)
            print "Set proxy to {}:{}.".format(proxy, port)
            profile.update_preferences()
        return profile
        
__author__ = "Guo, Aioria"