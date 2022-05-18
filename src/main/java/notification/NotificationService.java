package notification;

import notification.dao.Message;
import notification.service.IEmailService;
import notification.service.ILoggingService;
import notification.service.ISMSService;
import notification.exceptions.NotificationServiceException;

public class NotificationService {
	private IEmailService emailService;
	private ISMSService smsService;
	private ILoggingService loggingService;

	public NotificationService(IEmailService emailService, ISMSService smsService, ILoggingService loggingService) {
		this.emailService = emailService;
		this.smsService = smsService;
		this.loggingService = loggingService;
	}


	public void notifyAbout(Message message) {
		// - send email
		// - send SMS if email does not work (sendEmail returns false)
		// - if exceptions occurs, log information about exception
		boolean emailSent = false;
		try {
			boolean emailSuccess = this.emailService.sendEmail(message.getText(), message.getRecipient().getEmail());
			emailSent = emailSuccess;
			if(!emailSuccess) this.smsService.sendSMS(message.getText(), message.getRecipient().getPhoneNumber());
		}catch ( NotificationServiceException e) {
			if(!emailSent) {
				try{
					boolean sms = this.smsService.sendSMS(message.getText(), message.getRecipient().getPhoneNumber());
					if(!sms) {
						this.loggingService.log("Recipient could not be notified", "notification");
					}

				}catch (NotificationServiceException n) {
					this.loggingService.log(e.getMessage(), "notification");
					this.loggingService.log("Recipient could not be notified", "notification");
				}
			}
			this.loggingService.log(e.getMessage(), "notification");
		}

	}
}

