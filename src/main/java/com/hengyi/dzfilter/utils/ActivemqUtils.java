package com.hengyi.dzfilter.utils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.hengyi.dzfilter.model.MqMessage;

public class ActivemqUtils {
	
	private static ConnectionFactory connectionFactory; 
	private static String ChannelName = null;
	
	static {
		String activemq_user = PropertiesUtils.getValue("dzfilter.cluster.username");
		String activemq_pass = PropertiesUtils.getValue("dzfilter.cluster.password");
		String activemq_url = PropertiesUtils.getValue("dzfilter.cluster.activemq");
		ChannelName = PropertiesUtils.getValue("dzfilter.cluster.channel_name");
		connectionFactory = new ActiveMQConnectionFactory(activemq_user,activemq_pass, activemq_url);
	}

	
	/**
	 * 发送普通消息
	 */
	public static boolean SendObjectMessage(MqMessage mMqMessage) {
		Connection connection = null; 
        Session session = null; 
        Destination destination = null; 
        MessageProducer producer = null; 
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic(ChannelName);
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            ObjectMessage message = session.createObjectMessage(mMqMessage);
            producer.send(message);
            session.commit();
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
           return false;
        } finally {
            try {
                if (null != connection) {
                	producer.close();
                	session.close();
                    connection.close();
                }
            } catch (Throwable ignore) {
            }
        }
	}
	
	public static boolean SendObjectMessage(int id,int cmd,String server_id,String message) {
		MqMessage mqMessage = new MqMessage();
		mqMessage.setId(id);
		mqMessage.setServer_id(server_id);
		mqMessage.setMessage(message);
		mqMessage.setCmd(cmd);
		return SendObjectMessage(mqMessage);
	}

}
