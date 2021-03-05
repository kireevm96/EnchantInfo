// 
// Decompiled by Procyon v0.5.36
// 

package ru.kireevm;

import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.enchantments.Enchantment;
import java.util.HashSet;
import com.willfp.eco.util.StringUtils;
import java.util.Set;
import org.apache.commons.lang.WordUtils;
import com.willfp.ecoenchants.display.EnchantmentCache;
import com.samjakob.spigui.item.ItemBuilder;
import com.willfp.ecoenchants.enchantments.meta.EnchantmentType;
import com.willfp.ecoenchants.EcoEnchantsPlugin;
import com.willfp.ecoenchants.enchantments.EcoEnchant;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public class Main extends JavaPlugin implements Listener,
CommandExecutor {
  private static SpiGUI spiGUI;
  
  FileConfiguration config = getConfig();
  
  
  @Override
  public void onEnable() {
    config.addDefault("site_enchant", true);
    config.addDefault("page", "&cСтраница &c{currentPage} из {maxPage}");
    config.addDefault("menumain", "&cЗачарования");
    config.addDefault("backmain", "&6На главное меню");
    config.addDefault("menuall", "&6Показать все чары");
    config.addDefault("menunormal", "&6Показать обычные чары");
    config.addDefault("menuartifact", "&6Показать эффекты");
    config.addDefault("menucurse", "&6Показать проклятия");
    config.addDefault("menuspecial", "&6Показать особые чары");
    config.addDefault("menuspell", "&6Показать способности");
    config.addDefault("menusite", "&6Сайт с чарами");
    config.addDefault("menusitelore", "&bОткрывает сайт с чарами");
    config.addDefault("siteurl", "http://omegamc.ru/enchants/");
    config.addDefault("villager", "Житель: ");
    config.addDefault("yes", "Да");
    config.addDefault("no", "Нет");
    config.addDefault("back", "&a&l\u2190 Назад");
    config.addDefault("back_lore", "&aНажмите, чтобы вернуться к странице ");
    config.addDefault("next", "&a&lВперед \u2192");
    config.addDefault("next_lore", "&aНажмите, чтобы перейдти к странице ");
    
    config.options().copyDefaults(true);
    saveConfig();
    Bukkit.getServer().getPluginManager().registerEvents(this, this);
    spiGUI = new SpiGUI(this, config);
  }
  @Override
  public void onDisable() {
  }  
  private static EcoEnchantsPlugin PLUGIN = EcoEnchantsPlugin.getInstance();

  public List<String> WordWrapLore(String string)
    {
        StringBuilder sb = new StringBuilder(string);

        int i = 0;
        while (i + 40 < sb.length() && (i = sb.lastIndexOf(" ", i + 40)) != -1) {
            sb.replace(i, i + 1, "\n" + ChatColor.WHITE);
        }
        return Arrays.asList(sb.toString().split("\n"));
       
  }
  public List < String > printEnchant(EcoEnchant enchantment) {
    Set < String > conflictNames = new HashSet < >();

    Set < Enchantment > conflicts = enchantment.getConflicts();

    new HashSet < >(conflicts).forEach(enchantment1 ->{
      EcoEnchant ecoEnchant = EcoEnchants.getFromEnchantment(enchantment1);
      if (ecoEnchant != null && !ecoEnchant.isEnabled()) {
        conflicts.remove(enchantment1);
      }
    });

    conflicts.forEach((enchantment1 ->{
      if (EcoEnchants.getFromEnchantment(enchantment1) != null) {
        conflictNames.add(EcoEnchants.getFromEnchantment(enchantment1).getName());
      } else {
        conflictNames.add(PLUGIN.getLangYml().getString("enchantments." + enchantment1.getKey().getKey() + ".name"));
      }
    }));

    StringBuilder conflictNamesBuilder = new StringBuilder();
    conflictNames.forEach(name1 ->conflictNamesBuilder.append(name1).append(", "));
    String allConflicts = conflictNamesBuilder.toString();
    if (allConflicts.length() >= 2) {
      allConflicts = allConflicts.substring(0, allConflicts.length() - 2);
    } else {
      allConflicts = StringUtils.translate(PLUGIN.getLangYml().getString("no-conflicts"));
    }

    Set < Material > targets = enchantment.getTargetMaterials();
    Set < String > applicableItemsSet = new HashSet < >();

    if (PLUGIN.getConfigYml().getBool("commands.enchantinfo.show-target-group")) {
      enchantment.getTargets().forEach(target ->{
        String targetName = target.getName();
        targetName = targetName.toLowerCase();
        targetName = targetName.replace("_", " ");
        targetName = WordUtils.capitalize(targetName);
        applicableItemsSet.add(targetName);
      });
    } else {
      targets.forEach(material ->{
        String matName = material.toString();
        matName = matName.toLowerCase();
        matName = matName.replace("_", " ");
        matName = WordUtils.capitalize(matName);
        applicableItemsSet.add(matName);
      });
    }

    StringBuilder targetNamesBuilder = new StringBuilder();
    
    applicableItemsSet.forEach(name1 ->targetNamesBuilder.append(name1).append(", "));
    String allTargets = targetNamesBuilder.toString();
    if (allTargets.length() >= 2) {
      allTargets = allTargets.substring(0, allTargets.length() - 2);
    } else {
      allTargets = StringUtils.translate(PLUGIN.getLangYml().getString("no-targets"));
    }

    String maxLevel = String.valueOf(enchantment.getMaxLevel());

    String finalDescription = EnchantmentCache.getEntry(enchantment).getStringDescription();
    String finalTargets = allTargets;
    String finalConflicts = allConflicts;
    String finalMaxLevel = maxLevel;
    String isVillage = "\n" + ChatColor.BLUE + config.getString("villager") + (enchantment.isAvailableFromVillager() ? ChatColor.GREEN + config.getString("yes") : ChatColor.RED + config.getString("no") );
    List < String > list = new ArrayList < String > ();
    Arrays.asList(PLUGIN.getLangYml().getString("messages.enchantinfo").split("\\r?\\n")).forEach((string ->{
      string = string.replace("%name%: ", "").replace("%description%", ChatColor.WHITE + finalDescription).replace("%target%", ChatColor.WHITE + finalTargets).replace("%conflicts%", ChatColor.WHITE + finalConflicts).replace("%maxlevel%", ChatColor.WHITE + finalMaxLevel + isVillage);
      list.addAll(WordWrapLore(string));
    }));
    return list;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;
      List < EcoEnchant > enchantList = EcoEnchants.values().stream()
              .filter(EcoEnchant::isEnabled)
              .sorted((enchantment1, enchantment2) -> EnchantmentCache.getEntry(enchantment1).getRawName().compareToIgnoreCase(EnchantmentCache.getEntry(enchantment2).getRawName()))
              .collect(Collectors.toList());
      
      
      List < EcoEnchant > enchantListnormal = EcoEnchants.values().stream()
              .filter(enchantment -> EnchantmentCache.getEntry(enchantment).getType().equals(EnchantmentType.NORMAL)).filter(EcoEnchant::isEnabled)
              .sorted((enchantment1, enchantment2) -> EnchantmentCache.getEntry(enchantment1).getRawName().compareToIgnoreCase(EnchantmentCache.getEntry(enchantment2).getRawName()))
              .collect(Collectors.toList());
      List < EcoEnchant > enchantListartifact = EcoEnchants.values().stream()
              .filter(enchantment -> EnchantmentCache.getEntry(enchantment).getType().equals(EnchantmentType.ARTIFACT)).filter(EcoEnchant::isEnabled)
              .sorted((enchantment1, enchantment2) -> EnchantmentCache.getEntry(enchantment1).getRawName().compareToIgnoreCase(EnchantmentCache.getEntry(enchantment2).getRawName()))
              .collect(Collectors.toList());
      List < EcoEnchant > enchantListcurse = EcoEnchants.values().stream()
              .filter(enchantment -> EnchantmentCache.getEntry(enchantment).getType().equals(EnchantmentType.CURSE)).filter(EcoEnchant::isEnabled)
              .sorted((enchantment1, enchantment2) -> EnchantmentCache.getEntry(enchantment1).getRawName().compareToIgnoreCase(EnchantmentCache.getEntry(enchantment2).getRawName()))
              .collect(Collectors.toList());      
      List < EcoEnchant > enchantListspecial = EcoEnchants.values().stream()
              .filter(enchantment -> EnchantmentCache.getEntry(enchantment).getType().equals(EnchantmentType.SPECIAL)).filter(EcoEnchant::isEnabled)
              .sorted((enchantment1, enchantment2) -> EnchantmentCache.getEntry(enchantment1).getRawName().compareToIgnoreCase(EnchantmentCache.getEntry(enchantment2).getRawName()))
              .collect(Collectors.toList());       
       List < EcoEnchant > enchantListspell = EcoEnchants.values().stream()
              .filter(enchantment -> EnchantmentCache.getEntry(enchantment).getType().equals(EnchantmentType.SPELL)).filter(EcoEnchant::isEnabled)
              .sorted((enchantment1, enchantment2) -> EnchantmentCache.getEntry(enchantment1).getRawName().compareToIgnoreCase(EnchantmentCache.getEntry(enchantment2).getRawName()))
              .collect(Collectors.toList());
    
             
      if (enchantList.size() > 0) {
        SGMenu MenuAll = Main.spiGUI.create(config.getString("page"), 5);
        
        
        SGMenu Menunormal = Main.spiGUI.create(config.getString("page"), 5);
        SGMenu Menuartifact = Main.spiGUI.create(config.getString("page"), 5);
        SGMenu Menucurse = Main.spiGUI.create(config.getString("page"), 5);
        SGMenu Menuspecial = Main.spiGUI.create(config.getString("page"), 5);
        SGMenu Menuspell = Main.spiGUI.create(config.getString("page"), 5);


        Menunormal.setAutomaticPaginationEnabled(true);
        Menuartifact.setAutomaticPaginationEnabled(true);
        Menucurse.setAutomaticPaginationEnabled(true);
        Menuspecial.setAutomaticPaginationEnabled(true);
        Menuspell.setAutomaticPaginationEnabled(true);
        
        

        
        SGMenu MenuMain = Main.spiGUI.create(config.getString("menumain"), 1);
        MenuMain.setAutomaticPaginationEnabled(false);
        for (EcoEnchant enchant: enchantList) {
          MenuAll.addButton(new SGButton(
          createItemStack(Material.ENCHANTED_BOOK, 1, (byte) 0, ChatColor.translateAlternateColorCodes('&', ChatColor.YELLOW + enchant.getName()), translateAlternateColorCodesFromList('&', printEnchant(enchant)))
          ));
        }
        
        
        for (EcoEnchant enchant: enchantListnormal) {
          Menunormal.addButton(new SGButton(
          createItemStack(Material.ENCHANTED_BOOK, 1, (byte) 0, ChatColor.translateAlternateColorCodes('&', ChatColor.YELLOW + enchant.getName()), translateAlternateColorCodesFromList('&', printEnchant(enchant)))
          ));
        }
        for (EcoEnchant enchant: enchantListartifact) {
          Menuartifact.addButton(new SGButton(
          createItemStack(Material.ENCHANTED_BOOK, 1, (byte) 0, ChatColor.translateAlternateColorCodes('&', ChatColor.YELLOW + enchant.getName()), translateAlternateColorCodesFromList('&', printEnchant(enchant)))
          ));
        }
        for (EcoEnchant enchant: enchantListcurse) {
          Menucurse.addButton(new SGButton(
          createItemStack(Material.ENCHANTED_BOOK, 1, (byte) 0, ChatColor.translateAlternateColorCodes('&', ChatColor.YELLOW + enchant.getName()), translateAlternateColorCodesFromList('&', printEnchant(enchant)))
          ));
        }
        for (EcoEnchant enchant: enchantListspecial) {
          Menuspecial.addButton(new SGButton(
          createItemStack(Material.ENCHANTED_BOOK, 1, (byte) 0, ChatColor.translateAlternateColorCodes('&', ChatColor.YELLOW + enchant.getName()), translateAlternateColorCodesFromList('&', printEnchant(enchant)))
          ));
        }
        for (EcoEnchant enchant: enchantListspell) {
          Menuspell.addButton(new SGButton(
          createItemStack(Material.ENCHANTED_BOOK, 1, (byte) 0, ChatColor.translateAlternateColorCodes('&', ChatColor.YELLOW + enchant.getName()), translateAlternateColorCodesFromList('&', printEnchant(enchant)))
          ));
        }
     
        SGButton main = new SGButton(
                new ItemBuilder(Material.BARRIER)
                    .name(config.getString("backmain"))
                    .build()
            ).withListener(event -> {
                event.getWhoClicked().openInventory(MenuMain.getInventory());
            });
        
        
        MenuAll.setButtonbut(49, true,  main);
        
        Menunormal.setButtonbut(49, true, main);
        Menuartifact.setButtonbut(49, true, main);
        Menucurse.setButtonbut(49, true,  main);
        Menuspecial.setButtonbut(49, true, main);
        Menuspell.setButtonbut(49, true, main); 
        
                       
        MenuMain.setButton(0, 0, new SGButton(
                new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name(config.getString("menuall"))
                    .build()
            ).withListener(event -> {
                event.getWhoClicked().openInventory(MenuAll.getInventory());
            }));
        MenuMain.setButton(0, 2, new SGButton(
                new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name(config.getString("menunormal"))
                    .build()
            ).withListener(event -> {
                event.getWhoClicked().openInventory(Menunormal.getInventory());
            }));
        MenuMain.setButton(0, 3, new SGButton(
                new ItemBuilder(Material.FIREWORK_STAR)
                    .name(config.getString("menuartifact"))
                    .build()
            ).withListener(event -> {
                event.getWhoClicked().openInventory(Menuartifact.getInventory());
            }));
        MenuMain.setButton(0, 4, new SGButton(
                new ItemBuilder(Material.BOOK)
                    .name(config.getString("menucurse"))
                    .build()
            ).withListener(event -> {
                event.getWhoClicked().openInventory(Menucurse.getInventory());
            }));
        MenuMain.setButton(0, 5, new SGButton(
                new ItemBuilder(Material.BEACON)
                    .name(config.getString("menuspecial"))
                    .build()
            ).withListener(event -> {
                event.getWhoClicked().openInventory(Menuspecial.getInventory());
            }));
        MenuMain.setButton(0, 6, new SGButton(
                new ItemBuilder(Material.FIREWORK_ROCKET)
                    .name(config.getString("menuspell"))
                    .build()
            ).withListener(event -> {
                event.getWhoClicked().openInventory(Menuspell.getInventory());
            }));
        if (config.getBoolean("site_enchant")) {
         MenuMain.setButton(0, 8, new SGButton(
                new ItemBuilder(Material.PAINTING)
                    .name(config.getString("menusite"))
                    .lore(config.getString("menusitelore"))
                    .build()
            ).withListener(event -> {
                event.getWhoClicked().sendMessage(config.getString("siteurl"));
            }));
        }
         p.openInventory(MenuMain.getInventory());

      } else {}

    }
    else {
      sender.sendMessage("В§7В§l>> В§rВ§cThis command only works for players.");
    }
    return true;
  }
  public static List < String > translateAlternateColorCodesFromList(char colorChar, List < String > list) {
    List < String > new_list = new ArrayList < String > ();
    for (String line: list) {
      new_list.add(ChatColor.translateAlternateColorCodes(colorChar, line));
    }
    return new_list;
  }
  private ItemStack createItemStack(Material material, int amount, byte data, String displayName, List < String > lore) {
    ItemStack item = new ItemStack(material, amount, (short) data);
    ItemMeta meta = item.getItemMeta();
    if (displayName != null) {
      meta.setDisplayName(displayName);
    }
    if (lore != null && lore.size() > 0) {
      meta.setLore((List) lore);
    }
    item.setItemMeta(meta);
    return item;
  }


}