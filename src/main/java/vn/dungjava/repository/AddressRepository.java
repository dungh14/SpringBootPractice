package vn.dungjava.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dungjava.model.AddressEntity;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity,Integer> {

    AddressEntity findByUserIdAndAddressType(Long userId, Integer addressType);
}
