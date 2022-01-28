package com.les.ls.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Set;


public class CustomRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Set<String> roleSet = new HashSet<>();
        roleSet.add("admin");
        roleSet.add("admin1");
        roleSet.add("admin2");
        roleSet.add("admin3");
        AuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo(roleSet);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if ("admin".equals(token.getPrincipal())) {
            return new SimpleAuthenticationInfo("admin", "60000f64565380cd140871d2e59035ea", ByteSource.Util.bytes("123456"), this.getName());
        }
        return null;
    }

}
