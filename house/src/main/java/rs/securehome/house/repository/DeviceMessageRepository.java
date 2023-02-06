package rs.securehome.house.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import rs.securehome.common.enums.MessageType;
import rs.securehome.house.model.DeviceMessage;

import java.time.LocalDateTime;

public interface DeviceMessageRepository extends MongoRepository<DeviceMessage, String> {

    @Query("{ deviceId: { $in: ?0 }, deviceName: {$regex: '.*?1.*', $options: 'i'}, $or : [ { $expr: { $eq: ['?2', 'null'] } }, { messageType: ?2 } ], message: {$regex: '.*?3.*', $options: 'i'}, timestamp: {$gte: ?4, $lte: ?5} }")
    Page<DeviceMessage> getFilteredMessagesForDeviceList(int[] devices, String deviceName, MessageType messageType, String message, LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query(value = "{ deviceId: { $in: ?0 }, $or : [ { $expr: { $eq: ['?1', 'null'] } }, { messageType: ?1 } ], timestamp: {$gte: ?2, $lte: ?3} }", count = true)
    int getMessgeCount(int[] devices, MessageType messageType, LocalDateTime from, LocalDateTime to);

    @Query(value = "{" +
            "  timestamp: {" +
            "    $gt: ?0," +
            "    $lt:  $1" +
            "  }," +
            "  message: {" +
            "    $regex: $2" +
            "  }" +
            "}", count = true)
    int countFrequency(LocalDateTime startDate, LocalDateTime endDate, String messagePart);
}
