package unitTest;

import notification.NotificationService;
import notification.dao.Message;
import notification.dao.Recipient;
import notification.exceptions.NotificationServiceException;
import notification.service.IEmailService;
import notification.service.ILoggingService;
import notification.service.ISMSService;


import org.easymock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;


public class NotificationServiceTest extends EasyMockSupport{


    @Mock
    Recipient r;
    @Mock
    Message m;
    @TestSubject
    NotificationService ns;
    @Mock
    private IEmailService emailService;
    @Mock
    private ISMSService smsService;
    @Mock
    private ILoggingService loggingService;




    @BeforeEach()
    void setup() {
        emailService = createMock(IEmailService.class);
        smsService = createMock(ISMSService.class);
        loggingService = createMock(ILoggingService.class);


        r = new Recipient("raffaelprem@gmail.com","+431231231231");
        m = new Message("Test", r);
        ns = new NotificationService(emailService, smsService, loggingService);
    }

    @Test
    public void testEmailFailure() throws NotificationServiceException {
        expect(emailService.sendEmail("Test","raffaelprem@gmail.com")).andReturn(false);
        expect(smsService.sendSMS("Test", "+431231231231")).andReturn(true);
        replayAll();
        ns.notifyAbout(m);
        verifyAll();
    }
    @Test
    void testEmailSuccess() throws NotificationServiceException {
        // https://stackoverflow.com/questions/1972488/easymock-test-that-method-in-mock-isnt-called
        expect(emailService.sendEmail("Test","raffaelprem@gmail.com")).andReturn(true);
        replayAll();
        ns.notifyAbout(m);
        verifyAll();
    }

    @Test
    void testLoggingOnNoEmail() throws NotificationServiceException {
        expect(emailService.sendEmail("Test","raffaelprem@gmail.com")).andThrow(
                new NotificationServiceException("testError")
        );
        expect(smsService.sendSMS("Test", "+431231231231")).andReturn(true);

        expect(loggingService.log("testError", "notification")).andReturn(true);

       // loggingService.log("testError", "notification");
        replayAll();
        //EasyMock.expectLastCall();
        ns.notifyAbout(m);
        verifyAll();
    }

    @Test
    void testNeitherEmailNorSms() throws NotificationServiceException {
        expect(emailService.sendEmail("Test","raffaelprem@gmail.com")).andThrow(
                new NotificationServiceException("testError")
        );
        expect(loggingService.log("testError", "notification")).andReturn(true);
        expect(smsService.sendSMS("Test", "+431231231231")).andThrow(
                new NotificationServiceException("testError")
        );
        expect(loggingService.log("testError", "notification")).andReturn(true);
        expect(loggingService.log("Recipient could not be notified", "notification")).andReturn(true);

        replayAll();
        ns.notifyAbout(m);
        verifyAll();
    }



}
