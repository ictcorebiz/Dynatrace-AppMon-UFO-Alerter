
 /**
MIT License

Copyright (c) 2019 IctCoreBiz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
  **/ 

package biz.ictcore.dynatrace.ufo;

import com.dynatrace.diagnostics.pdk.*;
import java.util.logging.Logger;
import java.util.Collection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.lang.StringBuffer;



public class UfoActionPlugin implements ActionV2 {

	private static final Logger log = Logger.getLogger(UfoActionPlugin.class.getName());


	/**
	 * Initializes the Plugin. This method is called in the following cases:
	 * <ul>
	 * <li>before <tt>execute</tt> is called the first time for this
	 * scheduled Plugin</li>
	 * <li>before the next <tt>execute</tt> if <tt>teardown</tt> was called
	 * after the last execution</li>
	 * </ul>
	 * 
	 * <p>
	 * If the returned status is <tt>null</tt> or the status code is a
	 * non-success code then {@link #teardown(ActionEnvironment)} will be called
	 * next.
	 * 
	 * <p>
	 * Resources like sockets or files can be opened in this method.
	 * Resources like sockets or files can be opened in this method.
	 * @param env
	 *            the configured <tt>ActionEnvironment</tt> for this Plugin
	 * @see #teardown(ActionEnvironment)
	 * @return a <tt>Status</tt> object that describes the result of the
	 *         method call
	 */
	@Override
	public Status setup(ActionEnvironment env) throws Exception {
		// TODO
		return new Status(Status.StatusCode.Success);
	}

	/**
	 * Executes the Action Plugin to process incidents.
	 * 
	 * <p>
	 * This method may be called at the scheduled intervals, but only if incidents
	 * occurred in the meantime. If the Plugin execution takes longer than the
	 * schedule interval, subsequent calls to
	 * {@link #execute(ActionEnvironment)} will be skipped until this method
	 * returns. After the execution duration exceeds the schedule timeout,
	 * {@link ActionEnvironment#isStopped()} will return <tt>true</tt>. In this
	 * case execution should be stopped as soon as possible. If the Plugin
	 * ignores {@link ActionEnvironment#isStopped()} or fails to stop execution in
	 * a reasonable timeframe, the execution thread will be stopped ungracefully
	 * which might lead to resource leaks!
	 * 
	 * @param env
	 *            a <tt>ActionEnvironment</tt> object that contains the Plugin
	 *            configuration and incidents
	 * @return a <tt>Status</tt> object that describes the result of the
	 *         method call
	 */
	@Override
	public Status execute(ActionEnvironment env) throws Exception {
		/*
		// this sample shows how to receive and act on incidents
		*/
		String myColor=env.getConfigString("myColor");
		URL ufoUrl = env.getConfigUrl("myUfoUrl");
		StringBuffer stringbufferUfoUrl=new StringBuffer(ufoUrl.toString());
		String myAdditionalParams = env.getConfigString("myAdditionalParams");
		// Now we got the required stuff to build a real URL
	    stringbufferUfoUrl.append("?");
		if(myAdditionalParams.length()>0) {
			stringbufferUfoUrl.append(myAdditionalParams);
			stringbufferUfoUrl.append("&");
		}
		stringbufferUfoUrl.append(myColor);
		log.info("Colors requested: "+myColor);
		log.info("New URL: "+stringbufferUfoUrl.toString());
		try{
			// do the call
			URL url = new URL(stringbufferUfoUrl.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				log.severe("RTE: HTTP Errorcode: "+conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			conn.disconnect();

		} catch (MalformedURLException e) {

			log.severe("Malformed URL exception ("+stringbufferUfoUrl.toString()+")");
			throw(e);

//		} catch (IOException e) {

//			log.severe("IO Error");
//			throw(e);

		}

/*		Strange, but we don't need the details, just switch on the lights...

		Collection<Incident> incidents = env.getIncidents();
		for (Incident incident : incidents) {
			String message = incident.getMessage();
			log.info("Incident " + message + " triggered.");
			for (Violation violation : incident.getViolations()) {
				log.info("Measure " + violation.getViolatedMeasure().getName() + " violoated threshold.");
			}
		}
			*/

		return new Status(Status.StatusCode.Success);
	}

	/**
	 * Shuts the Plugin down and frees resources. This method is called in the
	 * following cases:
	 * <ul>
	 * <li>the <tt>setup</tt> method failed</li>
	 * <li>the Plugin configuration has changed</li>
	 * <li>the execution duration of the Plugin exceeded the schedule timeout</li>
	 * <li>the schedule associated with this Plugin was removed</li>
	 * </ul>
	 * <p>
	 * The Plugin methods <tt>setup</tt>, <tt>execute</tt> and
	 * <tt>teardown</tt> are called on different threads, but they are called
	 * sequentially. This means that the execution of these methods does not
	 * overlap, they are executed one after the other.
	 * 
	 * <p>
	 * Examples:
	 * <ul>
	 * <li><tt>setup</tt> (failed) -&gt; <tt>teardown</tt></li>
	 * <li><tt>execute</tt> starts, configuration changes, <tt>execute</tt>
	 * ends -&gt; <tt>teardown</tt><br>
	 * on next schedule interval: <tt>setup</tt> -&gt; <tt>execute</tt> ...</li>
	 * <li><tt>execute</tt> starts, execution duration timeout,
	 * <tt>execute</tt> stops -&gt; <tt>teardown</tt></li>
	 * <li><tt>execute</tt> starts, <tt>execute</tt> ends, schedule is
	 * removed -&gt; <tt>teardown</tt></li>
	 * </ul>
	 * Failed means that either an unhandled exception is thrown or the status
	 * returned by the method contains a non-success code.
	 * 
	 * <p>
	 * All by the Plugin allocated resources should be freed in this method.
	 * Examples are opened sockets or files.
	 * 
	 * @see #setup(ActionEnvironment)
	 */
	@Override
	public void teardown(ActionEnvironment env) throws Exception {
		// TODO
	}
}
