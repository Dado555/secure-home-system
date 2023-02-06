package rs.securehome.admin.repository.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.valueobjects.Log;
import rs.securehome.common.enums.LogType;


@Repository
public interface LogRepository extends MongoRepository<Log, String> {


    Page<Log> findAll(Pageable pageable);


    @Query("{" +
            "  $and: [" +
            "    {" +
            "      'timestamp': {" +
            "        $gt: {" +
            "               $date: ?2" +
                        "}" +
            "        $lt: {" +
            "           $date: ?3" +
            "}" +
            "      }" +
            "    }," +
            "    {" +
            "      logType: {" +
            "        $in: ?1" +
            "      }" +
            "    }," +
            "    {" +
            "      $or: [" +
            "      {" +
            "        'username':{" +
            "          $regex: ?0" +
            "        }" +
            "      }," +
            "      {" +
            "        'ipAddress': {" +
            "          $regex: ?0" +
            "        }" +
            "      }," +
            "      {" +
            "        'component': {" +
            "          $regex: ?0" +
            "        }" +
            "      }" +
            "    ]" +
            "    }" +
            "" +
            "  ]" +
            "}")
    Page<Log> findAllWithRegex(String regex, LogType[] logTypes, String timestampFrom, String timestampTo, Pageable pageable);


}
