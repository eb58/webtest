/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eb.filter;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.log4j.Logger;

/**
 * Web application lifecycle listener.
 *
 * @author A403163
 */
public class NewServletListener implements ServletContextListener, ServletContextAttributeListener, HttpSessionListener, HttpSessionAttributeListener {
  private  final Logger LOG = Logger.getRootLogger();

   @Override
   public void contextInitialized(ServletContextEvent sce) {
      LOG.info("contextInitialized - Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce) {
      LOG.info("contextDestroyed- Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void attributeAdded(ServletContextAttributeEvent event) {
      LOG.info("attributeAdded- Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void attributeRemoved(ServletContextAttributeEvent event) {
      LOG.info("attributeRemoved - Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void attributeReplaced(ServletContextAttributeEvent arg0) {
      LOG.info("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void sessionCreated(HttpSessionEvent se) {
      LOG.info("sessionCreated - Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void sessionDestroyed(HttpSessionEvent se) {
      LOG.info(" sessionDestroyed - Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void attributeAdded(HttpSessionBindingEvent event) {
      LOG.info("attributeAdded Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void attributeRemoved(HttpSessionBindingEvent event) {
      LOG.info("attributeRemoved Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void attributeReplaced(HttpSessionBindingEvent event) {
      LOG.info("attributeReplaced - Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
}
