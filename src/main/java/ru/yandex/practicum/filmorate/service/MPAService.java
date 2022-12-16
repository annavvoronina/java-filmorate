package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPA.MPAStorage;

import java.util.List;

@Slf4j
@Service
public class MPAService {

    private final MPAStorage mpaStorage;

    @Autowired
    public MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MPA getMPAById(int id) throws ObjectNotFoundException {
        return mpaStorage.getMPAById(id);
    }

    public List<MPA> getMPAs() {
        return mpaStorage.getMPAs();
    }
}
