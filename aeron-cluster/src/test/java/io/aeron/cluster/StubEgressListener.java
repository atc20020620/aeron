/*
 * Copyright 2017 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.cluster;

import io.aeron.cluster.client.EgressListener;
import io.aeron.cluster.codecs.SessionEventCode;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class StubEgressListener implements EgressListener
{
    public void sessionEvent(
        final long correlationId,
        final long clusterSessionId,
        final SessionEventCode code,
        final String detail)
    {
    }

    public void newLeader(
        final long correlationId,
        final long clusterSessionId,
        final long lastMessageTimestamp,
        final long clusterTermTimestamp,
        final long clusterMessageIndex,
        final long clusterTermId,
        final String leader)
    {
    }

    public void onMessage(
        final long correlationId,
        final long clusterSessionId,
        final long timestamp,
        final DirectBuffer buffer,
        final int offset,
        final int length,
        final Header header)
    {
    }
}
