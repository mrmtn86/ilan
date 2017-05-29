package model;

import java.util.ArrayList;
import java.util.List;

import static util.MatUtil.ortalamaHesapla;

/**
 * Created by mtn on 31.03.2017.
 */
public class ModelinIlanlari {


    public static String[] kusurluAciklamlar = {
            "ağır hasar kaydı var",
            "agir hasar kaydı mevcut", "" +
            "ÇEKME BELGELİ",
            "HASARLI AL",
            "Ağır Hasar",
            "Ağır Hasar Kayıtlıdır",
            "HASAR KAYDI İŞLENMİŞTİR",
            "SİGORTA ŞİŞİR",
            "HASAR KAYITLI",
            "sigorta şirketinden al",
            "agirhasarli",
            "Hasar kaydi vardir",
            "AĞIR HASAR KAYDI OLDUĞUNDAN",
            "agir hasarli",
            "AGIR HASAR KAYDI ",
            "komple dıştan boyalidir ",
            "kaskodan şişirilmiş ",
            "Az hasarlı ",
            "Az hasarlı ",
            "Hasar Kaydı Bulunmaktadır ",
            "agir hasar kayitlidir",
            "hasar kayıtlı",
            "hasar kaydı vardır",
            "Komple Temizlik Boyası",
            "SİGORTADAN PARA AL",
            "AĞIR HASAR",
            "Komple Boyalı",
            "komple boyali",
            "PERT KAYDI VAR",
            "agir hasar",
            "AGİR HASAR",
            "Ağır hasar kayıtlıdır",
            "komple boyalıdır",
            "hariç boya",
            "pert kayıtlıdır",
            "AGIR HASAR KAYITLIDIR",
            "ağır hasar kayıtlıdır",
            "tümden boyalıdır",
            "HASAR KAYDI MEVCUTTUR",
            "Hasar kaydı vardır",
            "hariç komple boyalı",
            "KOMPLE DIŞTAN BOYALI",
            "ağır hasar kaydı bulunmakta",
            "ARAÇ KOMPLE BOYALI",
            "KOMPLE DIŞTAN BOYALI ",
            "AĞIR HASARLI yazıyor",
            "HASAR KAYDI BULUNDUĞUNDAN ",
            "Hasar kaydı vardır ",
            "hasar  kayıtlıdır ",
            "hariç komple boyalı",
            "KOMPLE BOYATILMIŞTIR",
            "Boya takıntısı",
            "şişirilmiş hasar kaydı var",
            "agır hasarlı olarak geçiyor",
            "pert kayıtlı  aldım"};
    public List<ArabaIlan> arabaIlanList;

    public ModelinIlanlari() {
        arabaIlanList = new ArrayList<>();
    }

    public void ilanEkle(ArabaIlan arabaIlan) {

        arabaIlanList.add(arabaIlan);
    }

    public int ortalamaFiyatHespla() {

        List<Integer> sayilar = new ArrayList<>();

        for (ArabaIlan arabaIlan : arabaIlanList) {
            if (arabaIlan.getDurum() == null || arabaIlan.getDurum().ortalamadaKullan) {
                sayilar.add(arabaIlan.fiyat);
            }
        }
        return ortalamaHesapla(sayilar);
    }

    public int ortalamaKmHespla() {
        List<Integer> sayilar = new ArrayList<>();

        for (ArabaIlan arabaIlan : arabaIlanList) {
            if (arabaIlan.getDurum() == null || arabaIlan.getDurum().ortalamadaKullan) {
                sayilar.add(arabaIlan.km);
            }
        }
        return ortalamaHesapla(sayilar);
    }
}
