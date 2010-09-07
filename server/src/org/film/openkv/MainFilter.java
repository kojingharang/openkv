package org.film.openkv;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class MainFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		String serviceName = ((HttpServletRequest)req).getParameter("s");
		String t = ((HttpServletRequest)req).getParameter("t");
		String key = ((HttpServletRequest)req).getParameter("k");
		
	
		
		
		
		OpenKVController okvc = new OpenKVController();
		
		if(t == null || t.equals("")  || 
		   serviceName == null || serviceName.equals("")) {			
			ResponseData resData = okvc.returnErrorResponse((HttpServletRequest)req, "parameters are not set");
			res.getWriter().println(resData.toJSONP());
			return;
		}
		
		
		if(!okvc.privCheck(key, user, serviceName, t)) {
			ResponseData resData = okvc.returnErrorResponse((HttpServletRequest)req, 
					"operation " +  t + "is not allowed.");
			res.getWriter().println(resData.toJSONP());
			return;
		}

		
		chain.doFilter(req, res);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
	
	

	
	

	
}
