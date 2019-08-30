Arquiteura SIDCE

-SpringBoot 2.0

-SpringData

-SpringSecurity

-SpringActuator

-KeyCloack Adapter

-Swagger2

Acesso Swagger:

http://localhost:8080/sidce-backend/swagger-ui.html

Acesso Actuator Status do projeto:

http://localhost:8080/sidce-backend/actuator/health

Todos endpoints de monitoração:

http://localhost:8080/sidce-backend/actuator/

Acesso a Controle GIT

http://localhost:8080/sidce-backend/version

PS: A configuração do keycloack ficam no arquivo de prorpeties gerenciado pelo springboot. O Arquivo jboss-deployment-structure.xml, ignora as configurações do keycloack que estão no standalone.xml do jboss. Ou seja, qualquer configuração de keycloack deste projeto, está sendo ignorada no jboss. Em caso de remoção deste xml, a aplicação resultará em erros de conflitos de bibliotecas adapter keyclo