/**
* Global Sensor Networks (GSN) Source Code
* Copyright (c) 2006-2014, Ecole Polytechnique Federale de Lausanne (EPFL)
* 
* This file is part of GSN.
* 
* GSN is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* GSN is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with GSN.  If not, see <http://www.gnu.org/licenses/>.
* 
* File: src/gsn/acquisition2/server/SafeStorageServer.java
*
* @author Timotee Maret
* @author Ali Salehi
*
*/

package gsn.acquisition2.server;

import gsn.acquisition2.SafeStorage;
import gsn.acquisition2.SafeStorageDB;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

public class SafeStorageServer {
	
	public static final byte SS_START_MODE   = 1;
	public static final byte SS_CLEAN_MODE   = 100;
	
	public static transient Logger logger = LoggerFactory.getLogger(SafeStorageServer.class);
	
	private static final String DEFAULT_SAFESTORAGE_LOG4J_PROPERTIES = "conf/log4j_safestorage.properties";
	
	private IoAcceptor acceptor;
	
	public SafeStorageServer(int portNo) throws IOException, ClassNotFoundException, SQLException {
		SafeStorage ss  = new SafeStorage(portNo);
		acceptor = new SocketAcceptor();
		acceptor.getDefaultConfig().setThreadModel(ThreadModel.MANUAL);
				
		// Prepare the service configuration.
		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
		cfg.setReuseAddress(true);
		ObjectSerializationCodecFactory oscf = new ObjectSerializationCodecFactory();
	    oscf.setDecoderMaxObjectSize(oscf.getEncoderMaxObjectSize());	    
	    cfg.getFilterChain().addLast("codec",   new ProtocolCodecFilter(oscf));
	    // Create an unbounded Thread pool
	    ThreadPoolExecutor tpe = new ThreadPoolExecutor (0, Integer.MAX_VALUE, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>()) ;	    
	    cfg.getFilterChain().addLast("threadPool", new ExecutorFilter(tpe));
	    
	    logger.debug("MINA Decoder MAX: " + oscf.getDecoderMaxObjectSize() + " MINA Encoder MAX: " + oscf.getEncoderMaxObjectSize());
	    acceptor.bind(new InetSocketAddress(portNo),   new SafeStorageServerSessionHandler(ss), cfg);
		logger.info("Safe Storage Server is listening on port: " + portNo);
		
		
		
	}
  
  public void shutdown () {
	  acceptor.unbindAll();
  }
  
  public static void main(String[] args) throws Exception {   
	int safeStorageServerPort = Integer.parseInt(args[0]);
	int safeStorageControllerPort = Integer.parseInt(args[1]);
	byte safeStorageMode = Byte.parseByte(args[2]);
	switch (safeStorageMode) {
		case SS_START_MODE : {
			SafeStorageServer sss = new SafeStorageServer(safeStorageServerPort);
			new SafeStorageController(sss, safeStorageControllerPort);
			break;
		}
		case SS_CLEAN_MODE : {
			SafeStorageDB storage = new SafeStorageDB(safeStorageServerPort);
			storage.dropAllTables();
			logger.warn("SafeStorage database is now clean and empty.");
			break;
		}
		default : logger.error("Not valid SafeStorage mode >" + safeStorageMode + "<");
	}
  }
}
