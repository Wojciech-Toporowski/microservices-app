# Projekt AZNU

## Opis projektu

Projekt w dużym uproszczeniu symuluje system do rezerwowania naprawy samochodu. Składa się z dwóch usług: rezerwacji terminu oraz rezerwacji części. Jeżeli któraś z usług zwróci błąd, to "rezerwacja" jest usuwana też z drugiej.

Usługi zwracają błąd gdy:
 - liczba dni do naprawy jest podzielna przez 3
 - wybrana marka to Volkswagen

![architecture.png](architecture.png)

## Linki

Soap wsdl: [http://localhost:8084/service/part?wsdl](http://localhost:8084/service/part?wsdl)

Url for booking: [http://localhost:8080/bookRepair](http://localhost:8080/bookRepair)

Url for checking: [http://localhost:8080/checkBooking](http://localhost:8080/checkBooking)

Swagger doc: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

Komenda do budowania obrazów: ```mvn -Dmaven.test.skip install spring-boot:build-image``` (testy nie działają przy property dla dockera)

## Porty
- kafka: w kontenerze 9092, localhost: 9093
- user-application: 8080
- gateway: 8081
- kafka-rest-connector: 8082
- date-service-rest: 8083
- part-service-soap: 8084
- kafka-soap-connector: 8085

## TODO:
- [ ] Zmiany nazw endpointów na rzeczowniki
- [x] Zmiana nazwy grupy pakietu lib na spójną z resztą projektu
- [x] Automatyczne budowanie całości z maven
- [x] Dodanie parent project i hierarchii w pomach
- [ ] Dodanie 2 różnych baz danych - najlepiej relacyjnej i NoSql
- [ ] Zmiana usługi daty na datę a nie dni
- [x] Dodanie bootstrap CSS do frontendu
- [ ] Dokładniejsza dokumentacja całości po angielsku, uzasadnienie wybranej bezsensownej architektury uczelnią
- [ ] Dorzucenie orkiestracji przez Docker Swarm (skrypty w razie potrzeby)
- [ ] Dorzucenie orkiestracji przez kubernetes (skrypty w razie potrzeby)
- [ ] Healthcheck dla każdego modułu

