# SimpleMeterogicalApp
Using: 
- Java
- SpringBoot
- Postgres
- InfluxDB
- H2

Cílem projektu je vytvořit aplikaci pro ukládání a zobrazování meteorologických dat, která jsou průběžně stahována z http://www.openweathermap.com

Požadavky na technické řešení:
1. Maven pro sestavení :heavy_check_mark:
2. Spring Boot :heavy_check_mark:
3. Verzování přes Git :heavy_check_mark:

### Datový model(persistence)
- Stát
- Město
- Měření pro město
  - Expirace záznamů dle konfigurace(např. defaultně 14 dní)

Pro ukládání státu a města zvolte relační databázi - **Postgres** <br />
Pro ukládání měření zvolte vhodnou NoSQL databázi - **InfluxDB**

### REST
Aplikace bude obsahovat REST rozhraní pro přidávání, editaci a mazání států, měst a měření.A dále zobrazení aktuálních hodnot a průměru za poslední den, týden a 14 dní. :heavy_check_mark:
### Testování
Součástí řešení budou testy pro všechny operace volané přes REST API. :heavy_check_mark:
### Konfigurace
Musí být možno provádět externí konfiguraci –tj. veškerá konfigurace do properties souborů. :heavy_check_mark:
### Logování
Aplikace by měla využívat logovací systém Logback svýpisem do souboru (např. log.out). V případě chyby Vám bude zaslán pouze soubor log.out –výstup zkonzole pouze vpřípadě, že neprojdou testy. :heavy_check_mark:
### Sestavení
Výsledkem kompilace pomocí nástroje Maven musí být samostatně spustitelná webová aplikace –mimo IDE. :heavy_check_mark:
### CSV export a import
Měření pro jednotlivá města bude možné dávkově importovat a exportovat ve formátu CSV jako upload/download souboru. :heavy_check_mark:
### Data
Data je možné získávat zlibovolného veřejně dostupného API, například –sbezplatným přístupem při dodržení limitu 60 volání za sekundu. :heavy_check_mark:

### Další požadavky
1. Interval aktualizace dat by měl být konfigurovatelný (i sohledem na API limit) :heavy_check_mark:
2. Aplikaci by mělo být možné pustit vtzv. read-only modu, tj. lze provádět jen operace zobrazení a čtení dat a vypnutou aktualizací. :heavy_check_mark:
3. Konfigurovatelná expirace záznamů :heavy_check_mark:
