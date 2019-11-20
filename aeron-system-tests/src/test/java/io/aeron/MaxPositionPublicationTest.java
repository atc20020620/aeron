/*
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.protocol.DataHeaderFlyweight;
import org.agrona.CloseHelper;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.After;
import org.junit.Test;

import java.nio.ByteBuffer;

import static io.aeron.Publication.MAX_POSITION_EXCEEDED;
import static org.junit.Assert.assertEquals;

public class MaxPositionPublicationTest
{
    private static final int STREAM_ID = 7;
    private static final int MESSAGE_LENGTH = 32;

    private final UnsafeBuffer srcBuffer = new UnsafeBuffer(ByteBuffer.allocate(MESSAGE_LENGTH));

    private final MediaDriver driver = MediaDriver.launch(new MediaDriver.Context()
        .errorHandler(Throwable::printStackTrace)
        .dirDeleteOnStart(true)
        .dirDeleteOnShutdown(true)
        .threadingMode(ThreadingMode.SHARED));

    private final Aeron aeron = Aeron.connect();

    @After
    public void after()
    {
        CloseHelper.close(aeron);
        CloseHelper.close(driver);
    }

    @Test(timeout = 10_000)
    public void shouldPublishFromExclusivePublication()
    {
        final int initialTermId = -777;
        final int termLength = 64 * 1024;
        final long maxPosition = termLength * (Integer.MAX_VALUE + 1L);
        final long lastMessagePosition = maxPosition - (MESSAGE_LENGTH + DataHeaderFlyweight.HEADER_LENGTH);

        final String channelUri = new ChannelUriStringBuilder()
            .initialPosition(lastMessagePosition, initialTermId, termLength)
            .media("ipc")
            .validate()
            .build();

        try (Subscription ignore = aeron.addSubscription(channelUri, STREAM_ID);
            ExclusivePublication publication = aeron.addExclusivePublication(channelUri, STREAM_ID))
        {
            assertEquals(lastMessagePosition, publication.position());

            long resultingPosition = publication.offer(srcBuffer, 0, MESSAGE_LENGTH);
            while (resultingPosition < 0)
            {
                Thread.yield();
                SystemTest.checkInterruptedStatus();
                resultingPosition = publication.offer(srcBuffer, 0, MESSAGE_LENGTH);
            }

            assertEquals(maxPosition, publication.maxPossiblePosition());
            assertEquals(publication.maxPossiblePosition(), resultingPosition);
            assertEquals(MAX_POSITION_EXCEEDED, publication.offer(srcBuffer, 0, MESSAGE_LENGTH));
            assertEquals(MAX_POSITION_EXCEEDED, publication.offer(srcBuffer, 0, MESSAGE_LENGTH));
        }
    }
}
