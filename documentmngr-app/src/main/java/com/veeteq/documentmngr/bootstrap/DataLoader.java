package com.veeteq.documentmngr.bootstrap;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Category;
import com.veeteq.documentmngr.model.CategoryType;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Profile("test")
public class DataLoader implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public DataLoader(AccountRepository accountRepository, ItemRepository itemRepository) {
        this.accountRepository = accountRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadAccounts();
        loadItems();
    }

    private void loadAccounts() {
        var list = List.of(
                Account.builder().withId(1L).withName("ABC").build(),
                Account.builder().withId(2L).withName("BCD").build(),
                Account.builder().withId(3L).withName("CDE").build(),
                Account.builder().withId(4L).withName("DEF").build(),
                Account.builder().withId(5L).withName("EFG").build(),
                Account.builder().withId(6L).withName("FGH").build(),
                Account.builder().withId(7L).withName("GHI").build(),
                Account.builder().withId(8L).withName("HIJ").build(),
                Account.builder().withId(9L).withName("IJK").build(),
                Account.builder().withId(10L).withName("JKL").build()
        );
        accountRepository.saveAll(list);
    }

    private void loadItems() {
        Map<Long, Category> categories = Map.of(
                1L, Category.builder().withId(1L).withName("CAT_01").withCategoryType(CategoryType.Exp).build(),
                2L, Category.builder().withId(2L).withName("CAT_02").withCategoryType(CategoryType.Exp).build(),
                3L, Category.builder().withId(3L).withName("CAT_03").withCategoryType(CategoryType.Inc).build(),
                4L, Category.builder().withId(4L).withName("CAT_04").withCategoryType(CategoryType.Inc).build(),
                5L, Category.builder().withId(5L).withName("CAT_05").withCategoryType(CategoryType.Both).build()
        );

        var list = List.of(
                Item.builder().withId(101L).withName("Item_01").withCategory(categories.get(1L)).build(),
                Item.builder().withId(102L).withName("Item_02").withCategory(categories.get(2L)).build(),
                Item.builder().withId(103L).withName("Item_03").withCategory(categories.get(3L)).build(),
                Item.builder().withId(104L).withName("Item_04").withCategory(categories.get(4L)).build(),
                Item.builder().withId(105L).withName("Item_05").withCategory(categories.get(5L)).build(),
                Item.builder().withId(106L).withName("Item_06").withCategory(categories.get(1L)).build(),
                Item.builder().withId(107L).withName("Item_07").withCategory(categories.get(2L)).build(),
                Item.builder().withId(108L).withName("Item_08").withCategory(categories.get(3L)).build(),
                Item.builder().withId(109L).withName("Item_09").withCategory(categories.get(4L)).build(),
                Item.builder().withId(110L).withName("Item_10").withCategory(categories.get(5L)).build()
        );
        itemRepository.saveAll(list);
    }
}
