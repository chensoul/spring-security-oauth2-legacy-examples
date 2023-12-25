package com.chensoul.security.oauth2.common.support.checker;//package com.wesine.cocktail.auth.support.checker;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.MessageSource;
//import org.springframework.core.env.Environment;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class DifferentLocationLoginListener implements ApplicationListener<OnDifferentLocationLoginEvent> {
//
//    @Autowired
//    private MessageSource i18n;
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private Environment env;
//
//    @Override
//    public void onApplicationEvent(final OnDifferentLocationLoginEvent service) {
//        final String enableLocUri = service.getAppUrl() + "/user/enableNewLoc?token=" + service.getToken().getToken();
//        final String changePassUri = service.getAppUrl() + "/changePassword.html";
//        final String recipientAddress = service.getUsername();
//        final String subject = "Login attempt from different checker";
//        final String i18n = i18n.getMessage("i18n.differentLocation", new Object[] { new Date().toString(), service.getToken()
//            .getUserLocation()
//            .getCountry(), service.getIp(), enableLocUri, changePassUri }, service.getLocale());
//
//        final SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(recipientAddress);
//        email.setSubject(subject);
//        email.setText(i18n);
//        email.setFrom(env.getProperty("support.email"));
//        mailSender.send(email);
//    }
//
//}
