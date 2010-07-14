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
		
		String serviceName = req.getParameter("s");
		String keyName = req.getParameter("k");
		String cmd = req.getParameter("t");
		String callback = req.getParameter("callback");
		String reqId = req.getParameter("rid");
		
	    if(cmd != null && cmd.equals("put")) {
	    	doPut(req, resp);
	    	return;
	    }
	    
	    if(serviceName == null || serviceName.equals("")) {
	        sendErrorResponse(resp, "ServiceName is not set.", reqId, callback);
	        return;
	    }
	    if(keyName == null || keyName.equals("")) {
	    	sendErrorResponse(resp, "Key is not set.", reqId, callback);
	    	return;
	    }

		OpenKVController okvc = new OpenKVController();
		ResponseData resData = okvc.getData(serviceName, keyName, reqId, callback);
			
		resp.getWriter().println(resData.toJSONP());
		
	}
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp)  throws IOException {
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		String serviceName = req.getParameter("s");
		String keyName = req.getParameter("k");
		//String cmd = req.getParameter("t");
		String callback = req.getParameter("callback");
		String value = req.getParameter("v");
		String reqId = req.getParameter("rid");
		
	    if(serviceName == null || serviceName.equals("")) {
	        sendErrorResponse(resp, "ServiceName is not set.", reqId, callback);
	        return;
	    }
	    if(keyName == null || keyName.equals("")) {
	    	sendErrorResponse(resp, "Key is not set.", reqId, callback);
	    	return;
	    }
		
		OpenKVController okvc = new OpenKVController();
		
		ResponseData resData = okvc.putData(serviceName, keyName, value, reqId, callback);
		
		resp.getWriter().println(resData.toJSONP());
		
	}
	

	private void sendErrorResponse (HttpServletResponse resp, String message, String reqId, String callback) throws IOException {
		ResponseData resData = new ResponseData(ResponseData.NGCODE, message, reqId, callback);
		resp.getWriter().println(resData.toJSONP());
	}
}
