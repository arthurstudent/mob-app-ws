package com.appsdeveloperblog.app.ws.repository;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    boolean recordsCreated = false;

    @BeforeEach
    void setUp() {
        if (!recordsCreated) createRecords();
    }

    @AfterAll
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    final void testGetUser() {
        Pageable pageableRequest = PageRequest.of(1, 1);
        Page<UserEntity> page = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
        assertNotNull(page);

        List<UserEntity> userEntities = page.getContent();
        assertNotNull(userEntities);
        assertEquals(1, userEntities.size());
    }

    @Test
    final void testFindUserByFirstName() {
        String firstName = "Sergey";
        List<UserEntity> users = userRepository.findUserByFirstName(firstName);
        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertEquals(user.getFirstName(), firstName);
    }

    @Test
    final void testFindUserByLastName() {
        String lastName = "Kargopolov";
        List<UserEntity> users = userRepository.findUserByLastName(lastName);
        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertEquals(user.getLastName(), lastName);
    }

    @Test
    final void testFindUsersByKeyword() {
        String keyword = "erg";
        List<UserEntity> users = userRepository.findUsersByKeyword(keyword);
        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertTrue(
                user.getLastName().contains(keyword) ||
                        user.getFirstName().contains(keyword)
        );
    }

    @Test
    final void testFindUserFirstNameAndLastNameByKeyword() {
        String keyword = "erg";
        List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);
        assertNotNull(users);
        assertEquals(2, users.size());

        Object[] user = users.get(0);

        assertEquals(2, user.length);

        String userFirstName = String.valueOf(user[0]);
        String userLastName = String.valueOf(user[1]);

        assertNotNull(userFirstName);
        assertNotNull(userLastName);
    }

    @Test
    final void testUpdateUserEmailVerificationStatus() {
        boolean newEmailVerificationStatus = true;
        userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, "1144332211");

        UserEntity storedUserDetails = userRepository.findByUserId("1144332211");

        boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();

        assertEquals(storedEmailVerificationStatus, newEmailVerificationStatus);

    }

    @Test
    final void testFindUserEntityByUserId() {
        String userId = "1111222233";
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);

        assertNotNull(userEntity);
        assertEquals(userEntity.getUserId(), userId);
    }

    @Test
    final void testGetUserEntityFullNameById() {
        String userId = "1111222233";
        List<Object[]> records = userRepository.getUserEntityFullNameById(userId);

        assertNotNull(records);
        assertEquals(1, records.size());

        Object[] userDetails = records.get(0);

        String firstName = String.valueOf(userDetails[0]);
        String lastName = String.valueOf(userDetails[1]);

        assertNotNull(firstName);
        assertNotNull(lastName);
    }

    @Test
    final void testUpdateUserEntityEmailVerificationStatus() {
        boolean newEmailVerificationStatus = true;
        userRepository.updateUserEntityEmailVerificationStatus(newEmailVerificationStatus, "1111222233");

        UserEntity storedUserDetails = userRepository.findByUserId("1111222233");

        boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();

        assertEquals(storedEmailVerificationStatus, newEmailVerificationStatus);

    }

    private void createRecords() {
        // Prepare User Entity
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("Sergey");
        userEntity.setLastName("Kargopolov");
        userEntity.setUserId("1144332211");
        userEntity.setEncryptedPassword("xxx");
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationStatus(true);

        // Prepare User Addresses
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setType("shipping");
        addressEntity.setAddressId("2233445566");
        addressEntity.setCity("Vancouver");
        addressEntity.setCountry("Canada");
        addressEntity.setPostalCode("ABCCDA");
        addressEntity.setStreetName("123 Street Address");

        List<AddressEntity> addresses = new ArrayList<>();
        addresses.add(addressEntity);

        userEntity.setAddresses(addresses);

        userRepository.save(userEntity);


        // Prepare User Entity
        UserEntity userEntity2 = new UserEntity();
        userEntity2.setFirstName("Sergey");
        userEntity2.setLastName("Kargopolov");
        userEntity2.setUserId("1111222233");
        userEntity2.setEncryptedPassword("xxx");
        userEntity2.setEmail("test@test.com");
        userEntity2.setEmailVerificationStatus(true);

        // Prepare User Addresses
        AddressEntity addressEntity2 = new AddressEntity();
        addressEntity2.setType("shipping");
        addressEntity2.setAddressId("3322113344");
        addressEntity2.setCity("Vancouver");
        addressEntity2.setCountry("Canada");
        addressEntity2.setPostalCode("ABCCDA");
        addressEntity2.setStreetName("123 Street Address");

        List<AddressEntity> addresses2 = new ArrayList<>();
        addresses2.add(addressEntity2);

        userEntity2.setAddresses(addresses2);

        userRepository.save(userEntity2);

        recordsCreated = true;
    }
}
