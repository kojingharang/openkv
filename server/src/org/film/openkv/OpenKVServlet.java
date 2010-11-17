package org.film.openkv;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.*;

import net.arnx.jsonic.JSON;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


@SuppressWarnings("serial")
public class OpenKVServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		OpenKVController okvc = new OpenKVController();
		ResponseData resData = okvc.process(req);
	    if(resData!=null)
	    {
	    	resp.getWriter().println(resData.toJSONP());
	    }
	}
}
