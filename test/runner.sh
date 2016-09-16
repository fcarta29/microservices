#!/bin/bash
testrunner.sh -PRS_HOST=${SERVICE} -PUS_HOST=${SERVICE} \
    -PAS_HOST=${SERVICE} -PJOURNAL_HOST=${SERVICE} Q3-Training-Tests-soapui-project.xml 
