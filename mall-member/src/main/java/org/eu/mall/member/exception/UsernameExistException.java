package org.eu.mall.member.exception;

public class UsernameExistException extends RuntimeException {
    public UsernameExistException() {
        super("用户名存在");
    }
}
