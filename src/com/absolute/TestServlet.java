package com.absolute;


import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import redis.clients.jedis.Jedis;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("doGet");
		Enumeration<String> pNames = request.getParameterNames();
		System.out.println("pNames"+pNames.toString());
		while(pNames.hasMoreElements()){
			String n = pNames.nextElement();
			System.out.println("parameter name:"+n+" value:"+request.getParameter(n));
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("POST");
		Enumeration<String> pNames = request.getParameterNames();
		while(pNames.hasMoreElements()){
			String n = pNames.nextElement();
			System.out.println("parameter name:"+n+" value:"+request.getParameter(n));	
		}
		Jedis jedis = new Jedis("localhost");
		jedis.set("groupID555", "555");

	}

}
