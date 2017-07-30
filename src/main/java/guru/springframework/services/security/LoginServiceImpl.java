package guru.springframework.services.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import guru.springframework.domain.User;
import guru.springframework.services.UserService;

@Service
public class LoginServiceImpl implements LoginService {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Scheduled(fixedRate = 60000)
	@Override
	public void resetLoginFailedAttempts() {
		System.out.println("Checking for locked accounts...");

		List<User> list = (List<User>) userService.listAll();

		list.forEach(user -> {
			if (user.getEnabled() != true && user.getFailedLoginAttempts() > 0) {
				System.out.println("Resetting failedLoginAttempts for user : " + user.getUsername());
				user.setFailedLoginAttempts(0);
				user.setEnabled(true);
				userService.saveOrUpdate(user);
			}
		});

	}

}
