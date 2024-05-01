package com.kodilla.csvdateconverter.configuration;

import com.kodilla.csvdateconverter.domain.PersonWithAge;
import com.kodilla.csvdateconverter.domain.PersonWithBirthdate;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.Period;

public class PersonProcessor implements ItemProcessor<PersonWithBirthdate, PersonWithAge> {

    @Override
    public PersonWithAge process(PersonWithBirthdate item) {
        int age = Period.between(LocalDate.parse(item.getBirthdate()), LocalDate.now()).getYears();
        return new PersonWithAge(item.getId(), item.getName(), item.getFamilyName(), age);
    }
}
