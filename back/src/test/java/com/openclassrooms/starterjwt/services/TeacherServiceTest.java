package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class TeacherServiceTest {
    @InjectMocks//injection des mocks dans le service
    private TeacherService teacherService;

    @Mock
    private TeacherRepository mockTeacherRepository;

    @BeforeEach
    void setUp() {
        // ce code garantit que les mocks sont prêts à être utilisés dans chaque test.
        MockitoAnnotations.openMocks(this);
    }
   @Test
    public void testTeacherService(){
        assertEquals(mockTeacherRepository, teacherService.getTeacherRepository());
    }

    @Test
    public void testFindTeacherById (){

        //Long teacherId = 1L;
        // création d'une instance de la classe Teacher
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        //spécification que lors de l'appel de la méthode findById() avec l'identifiant du teacher,
        // le mock doit retourner un objet Optional contenant ce teacher.
        when(mockTeacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        // appelle de la méthode findById() de teacherService en lui passant l'identifiant du teacher,
        // le résultat stocké dans une variable result.
        Teacher result = teacherService.findById(teacher.getId());

        //verify(mockTeacherRepository, times(1)).findById(teacher.getId());

        // utilisation d'assertion assertEquals() pour comparer le professeur récupéré avec le professeur créé précédemment.
        assertEquals(teacher, result);

    }

    @Test
    public void testFindAllTeachers(){
        //création d'une liste de professeurs attendus
        List<Teacher> expectedTeachers = new ArrayList<>();
        expectedTeachers.add(new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()));
        expectedTeachers.add(new Teacher(2L, "Smith", "Jane", LocalDateTime.now(), LocalDateTime.now()));

        //spécification que lors de l'appel de la méthode findAll(), le mock doit retourner la liste de teachers attendus.
        when(mockTeacherRepository.findAll()).thenReturn(expectedTeachers);

        //appelle de la méthode findAll() de teacherService pour obtenir la liste réelle de teachers (actualTeachers).
        List<Teacher> actualTeachers = teacherService.findAll();

        //Comparation de la taille de la liste attendue (expectedTeachers) avec la taille de la liste réelle (actualTeachers) pour s'assurer qu'elles sont égales
        assertEquals(expectedTeachers.size(), actualTeachers.size());

        // Le test boucle sur chaque élément des deux listes et compare les attributs (identifiant, nom, prénom, date de création et date de mise à jour) de chaque teacher.
        for (int i = 0; i < expectedTeachers.size(); i++) {
            assertEquals(expectedTeachers.get(i).getId(), actualTeachers.get(i).getId());
            assertEquals(expectedTeachers.get(i).getLastName(), actualTeachers.get(i).getLastName());
            assertEquals(expectedTeachers.get(i).getFirstName(), actualTeachers.get(i).getFirstName());
            assertEquals(expectedTeachers.get(i).getCreatedAt(), actualTeachers.get(i).getCreatedAt());
            assertEquals(expectedTeachers.get(i).getUpdatedAt(), actualTeachers.get(i).getUpdatedAt());
        }
        // Verifying that findAll() method is called exactly once
        //verify(mockTeacherRepository, times(1)).findAll();
    }

}