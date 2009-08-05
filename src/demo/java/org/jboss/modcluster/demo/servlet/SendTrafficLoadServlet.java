/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.modcluster.demo.servlet;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author Paul Ferraro
 *
 */
public class SendTrafficLoadServlet extends LoadServlet
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -8586013739155819909L;
   private static final String SIZE = "size";
   
   /**
    * @{inheritDoc}
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      int size = Integer.parseInt(request.getParameter(SIZE)) * 1000;
      
      response.getOutputStream().write(new byte[size]);
      response.flushBuffer();
   }

   /**
    * @{inheritDoc}
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      int duration = Integer.parseInt(this.getParameter(request, DURATION, DEFAULT_DURATION));
      
      String size = this.getParameter(request, SIZE, "100");
      
      HttpClient client = new HttpClient();
      HttpMethod method = new PostMethod(this.createLocalURL(request, Collections.singletonMap(SIZE, size)));

      for (int i = 0; i < duration; ++i)
      {
         long start = System.currentTimeMillis();
         
         client.executeMethod(method);

         long ms = 1000 - (System.currentTimeMillis() - start);
         
         if (ms > 0)
         {
            try
            {
               Thread.sleep(ms);
            }
            catch (InterruptedException e)
            {
               Thread.currentThread().interrupt();
            }
         }
      }
      
      this.writeLocalName(request, response);
   }
}
