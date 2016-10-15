package com.vmware.pso.samples.services.error.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;
import com.vmware.pso.samples.core.dto.ErrorDto;

@RestController
@RequestMapping("/api/errors")
public class ErrorsController {

    private final static Logger LOG = Logger.getLogger(ErrorsController.class);

    @Autowired
    private SendGrid sendGridClient;

    @Autowired
    private SendGrid.Email sendGridEmail;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    final public @ResponseBody Response testErrorEmail(@RequestBody final ErrorDto errorDto) {
        Response response = null;
        try {
            sendGridEmail.setText(errorDto.toString());
            response = sendGridClient.send(sendGridEmail);
        } catch (final SendGridException sge) {
            LOG.error(sge);
        }

        return response;
    }

}
