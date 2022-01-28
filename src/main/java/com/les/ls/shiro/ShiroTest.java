package com.les.ls.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;

public class ShiroTest {
    public static void main(String[] args) {
        /*DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(new IniRealm("classpath:shiro/shiro.ini"));
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken("admin", "admin");
        System.out.println(subject.isAuthenticated());
        subject.login(usernamePasswordToken);
        System.out.println(subject.isAuthenticated());*/

        /*Md5Hash md5Hash = new Md5Hash("admin", "123456", 1024);
        System.out.println(md5Hash.toHex());*/

        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        CustomRealm realm = new CustomRealm();
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");
        matcher.setHashIterations(1024);
        realm.setCredentialsMatcher(matcher);
        securityManager.setRealm(realm);
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("admin", "admin");
        subject.login(token);
        System.out.println(subject.isAuthenticated());
        if(subject.isAuthenticated()){
            boolean result = subject.hasRole("admin");
            System.out.println(result);
        }
    }
}
