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
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with GSN. If not, see <http://www.gnu.org/licenses/>.
*
* File: gsn-tiny/src/tinygsn/controller/AbstractController.java
*
* @author Do Ngoc Hoan
*/


package tinygsn.controller;


import tinygsn.beans.StreamElement;
import tinygsn.storage.StorageManager;
import android.app.Activity;

public abstract class AbstractController  {

  /**
	 * 
	 */
	private int id;

public abstract void startLoadVSList();
//
//  public abstract void tinygsnStop();
//
	public abstract void consume(StreamElement streamElement);
//
	public abstract StorageManager getStorageManager();
	
	public abstract Activity getActivity();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}