package com.geniusgarden.server.Pages;

public class getHtml {
    public static String getOtpPage(String otp){
        String htmlBody = "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                + "    <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px;'>"
                + "        <div style='text-align: center;'>"
                + "            <img src='https://i.ibb.co/RBqvFmg/GENIUS-GARDERN.png' alt='GeniusGarden Logo' style='width: 150px; border-radius: 50%;'/>"
                + "            <h1 style='color: #4CAF50;'>Welcome to GeniusGarden!</h1>"
                + "        </div>"
                + "        <p style='font-size: 16px; color: #333;'>Dear User,</p>"
                + "        <p style='font-size: 16px; color: #333;'>Your OTP code is:</p>"
                + "        <p style='font-size: 24px; font-weight: bold; color: #4CAF50;'>" + otp + "</p>"
                + "        <p style='font-size: 16px; color: #333;'>Please use this OTP to verify your email address. It will expire shortly.</p>"
                + "        <hr style='border-top: 1px solid #ddd;'/>"
                + "        <p style='font-size: 14px; color: #999; text-align: center;'>Thank you for playing GeniusGarden!</p>"
                + "    </div>"
                + "</body>"
                + "</html>";
        return htmlBody;
    }
}
