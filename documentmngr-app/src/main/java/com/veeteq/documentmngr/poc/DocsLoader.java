package com.veeteq.documentmngr.poc;

import com.veeteq.documentmngr.model.DocumentType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DocsLoader implements CommandLineRunner {

    private final DocRepository docRepository;

    public DocsLoader(DocRepository docRepository) {
        this.docRepository = docRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var entity1 = new Doc()
                .setParam1("Witold")
                .setParam2("Wojnarowicz");
        var left1 = new Left().setData("left data");
        var docItem1 = new DocItem()
                .setType("Left")
                .setLeft(left1);
        entity1.addToDocItems(docItem1);
        var saved1 = docRepository.save(entity1);
        System.out.println(saved1);
        System.out.println("Count: " + docRepository.count());

        var entity2 = new Doc()
                .setParam1("Magda")
                .setParam2("Wojnarowicz");
        var left2 = new Left().setData("left data");
        var right2 = new Right().setData("right data");
        var docItem21 = new DocItem()
                .setType("Left")
                .setLeft(left2);
        entity2.addToDocItems(docItem21);
        var docItem22 = new DocItem()
                .setType("Right")
                .setRight(right2);
        entity2.addToDocItems(docItem22);
        var saved2 = docRepository.save(entity2);
        System.out.println(saved2);
        System.out.println("Count: " + docRepository.count());
    }
}
