/*
 * @ (#) DataInitializer.java       1.0     01/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.config;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 01/04/2025
 * @version:    1.0
 */

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.authservice.model.entity.Role;
import vn.edu.iuh.fit.authservice.model.entity.User;
import vn.edu.iuh.fit.authservice.repository.RoleRepository;
import vn.edu.iuh.fit.authservice.repository.UserRepository;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Kiểm tra và thêm các Role nếu chưa có
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("USER");
        createRoleIfNotExists("LANDLORD");

        // Kiểm tra xem có dữ liệu người dùng chưa, nếu chưa thì thêm user admin
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .email("admin@gmail.com")
                    .phoneNumber("0123456789")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Set.of(roleRepository.findByRoleName("ADMIN").orElseThrow()))
                    .verified(true)
                    .build();

            userRepository.save(admin);

            System.out.println("✅ Dữ liệu mẫu đã được chèn vào database!");
        } else {
            System.out.println("⚠️ Dữ liệu đã tồn tại, không cần insert lại.");
        }
    }

    private void createRoleIfNotExists(String roleName) {
        // Kiểm tra xem Role đã tồn tại chưa
        if (!roleRepository.existsByRoleName(roleName)) {
            // Nếu chưa có thì thêm mới
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
            System.out.println("✅ Đã thêm role: " + roleName);
        } else {
            System.out.println("⚠️ Role " + roleName + " đã tồn tại.");
        }
    }
}

