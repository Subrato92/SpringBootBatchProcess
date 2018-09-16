package com.scope.banking.processors;

import org.springframework.batch.item.ItemProcessor;

import com.scope.banking.models.usrLogin;

public class UserItemProcessor implements ItemProcessor<usrLogin, usrLogin> {

	 @Override
	 public usrLogin process(usrLogin user) throws Exception {
		 usrLogin test =new usrLogin();
		 test.setPass(user.getPass());
		 test.setName(user.getName().toUpperCase());
		 System.out.println(user.getName());
	  return test;
	 }

}
