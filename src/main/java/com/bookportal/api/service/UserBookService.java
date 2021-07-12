package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.User;
import com.bookportal.api.entity.UserBook;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.model.enums.UserBookEnum;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.UserBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBookService {
    private final UserBookRepository userBookRepository;
    private final UserService userService;
    private final BookService bookService;

    public UserBook save(Long bookId, Long enumId) {
        UserBookEnum userBookEnum = getEnum(String.valueOf(enumId));
        UserBook userBook = getUserBookObj(bookId);
        userBook.setType(userBookEnum);
        UserBook exist = checkIfExist(bookId, userBookEnum);
        if (exist != null) {
            exist.setActive(!exist.isActive());
            return userBookRepository.save(exist);
        }
        return userBookRepository.save(userBook);
    }

    public Map<String, Boolean> findTypesForBook(Long bookId) {
        Map<String, Boolean> map = new HashMap<>();
        Optional<UserBook> willRead = userBookRepository.findByUser_IdAndBook_IdAndType(getUser().getId(), bookId, UserBookEnum.WILL_READ);
        Optional<UserBook> haveRead = userBookRepository.findByUser_IdAndBook_IdAndType(getUser().getId(), bookId, UserBookEnum.HAVE_READ);
        map.put(UserBookEnum.WILL_READ.getValue(), false);
        map.put(UserBookEnum.HAVE_READ.getValue(), false);
        if (willRead.isPresent() && willRead.get().isActive()) {
            map.put(UserBookEnum.WILL_READ.getValue(), true);
        }
        if (haveRead.isPresent() && haveRead.get().isActive()) {
            map.put(UserBookEnum.HAVE_READ.getValue(), true);
        }
        return map;
    }

    private UserBook checkIfExist(Long bookId, UserBookEnum userBookEnum) {
        Long userId = getUser().getId();
        Optional<UserBook> optional = userBookRepository.findByUser_IdAndBook_IdAndType(userId, bookId, userBookEnum);
        return optional.orElse(null);
    }

    private UserBook getUserBookObj(Long bookId) {
        Book book = getBook(bookId);
        User user = getUser();
        UserBook userBook = new UserBook();
        userBook.setBook(book);
        userBook.setUser(user);
        return userBook;
    }

    private UserBookEnum getEnum(String type) {
        boolean exist = UserBookEnum.isExist(String.valueOf(type));
        if (exist) {
            return UserBookEnum.findByValue(type);
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.TYPE.getValue());
    }

    private User getUser() {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            return currentUser.get();
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.USER.getValue());
    }

    private Book getBook(Long id) {
        return bookService.findByIdAndActiveTrueAndIsPublishedTrue(id);
    }
}
