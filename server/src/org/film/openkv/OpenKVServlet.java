package org.film.openkv;

import java.io.IOException;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class OpenKVServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		//resp.setContentType("text/plain");
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		String cmd = req.getParameter("t");
		
	    if(cmd != null && cmd.equals("put")) {
	    	doPut(req, resp);
	    	return;
	    }
	    else if(cmd != null && cmd.equals("delete")) {
	    	doDelete(req, resp);
	    	return;
	    }
 


		OpenKVController okvc = new OpenKVController();
		ResponseData resData = okvc.getData(req);
			
		resp.getWriter().println(resData.toJSONP());
		
	}
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp)  throws IOException {
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		
		
		OpenKVController okvc = new OpenKVController();
		
		ResponseData resData = okvc.putData(req);
		
		resp.getWriter().println(resData.toJSONP());
		
	}

	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		OpenKVController okvc = new OpenKVController();
		
		ResponseData resData = okvc.deleteData(req);
		
		resp.getWriter().println(resData.toJSONP());
		
	}


}
