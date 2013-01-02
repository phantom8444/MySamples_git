package com.MySamples.MVCSample;

import com.MySamples.MVCSample.model.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
@SuppressWarnings("serial")
public class MVCSampleServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String c = req.getParameter("color");
		BeerExpert be = new BeerExpert();
		ArrayList<String>  result = be.getBrands(c);
		
//		resp.setContentType("text/html");
//		PrintWriter out = resp.getWriter();
//		
//		out.println("Beer Selection Advice<br>");
//		
//		Iterator<String> it = result.iterator();
//		
//		while(it.hasNext()) {
//			out.print("<br> try : " + it.next());
//		}
		
		req.setAttribute("styles", result);
		
		RequestDispatcher view = req.getRequestDispatcher("Result.jsp");
		view.forward(req, resp);
	}
}
