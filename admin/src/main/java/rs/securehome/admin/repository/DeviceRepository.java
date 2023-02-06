package rs.securehome.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    @Query("select d from Device d where d.realEstate.id = :realEstateId order by d.id asc")
    Page<Device> getAllDevicesForRealEstate(Pageable pageable, Integer realEstateId);
}
