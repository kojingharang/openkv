package org.film.openkv;

import java.io.IOException;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class OpenKVServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/x-javascript");
		resp.setCharacterEncoding("UTF-8");
		
		OpenKVController okvc = new OpenKVController();
		ResponseData resData = okvc.process(req);
	    if(resData!=null)
	    {
	    	resp.getWriter().println(resData.toJSONP());
	    }
	}
}
