/**
 * Copyright (c) 2012, Eirik Brandtzæg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  *  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  *  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL EIRIK BRANDTZÆG BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package no.eirikb.gwtchannelapi.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;

/**
 * @author Eirik Brandtzæg <eirikb@eirikb.no>
 */
public class Channel {
    private List<ChannelListener> channelListeners;
    private String channelKey;

    /**
     *
     * @param channelKey key of the channel.
     */
    public Channel(String channelKey) {
        this.channelKey = channelKey;
        channelListeners = new ArrayList<ChannelListener>();
    }

    private void onMessage(String encoded) {
        SerializationStreamFactory ssf = GWT.create(ChannelService.class);
        try {
            Message message = (Message) ssf.createStreamReader(encoded).readObject();
            for (int i = 0; i < channelListeners.size(); i++) {
                channelListeners.get(i).onReceive(message);
            }
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }
    
    private void onOpen() {
        for (int i = 0; i < channelListeners.size(); i++) {
            channelListeners.get(i).onOpen();
        }
    }

    private void onError(int code, String description) {
        for (int i = 0; i < channelListeners.size(); i++) {
            channelListeners.get(i).onError(code, description);
        }
    }

    private void onClose() {
        for (int i = 0; i < channelListeners.size(); i++) {
            channelListeners.get(i).onClose();
        }
    }

    /**
     * Join the channel
     */
    public void join() {
        join(channelKey);
    }

    /**
     * Add a ChannelListener that will respond on events
     * 
     * @param channelListener
     */
    public void addChannelListener(ChannelListener channelListener) {
        channelListeners.add(channelListener);
    }

    public void removeChannelListener(ChannelListener channelListener) {
        channelListeners.remove(channelListener);
    }

    private native void join(String channelKey) /*-{
        var channel = new $wnd.goog.appengine.Channel(channelKey);
        var socket = channel.open();
        var self = this;
        socket.onmessage = function(evt) {
            var data = evt.data;
            self.@no.eirikb.gwtchannelapi.client.Channel::onMessage(Ljava/lang/String;)(data);
        }
        
        socket.onopen = function() {
            self.@no.eirikb.gwtchannelapi.client.Channel::onOpen()();
        };

        socket.onerror = function(error) {
            self.@no.eirikb.gwtchannelapi.client.Channel::onError(ILjava/lang/String;)(error.code, error.description);
        };

        socket.onclose = function() {
            self.@no.eirikb.gwtchannelapi.client.Channel::onClose()();
        };
    }-*/;
}
