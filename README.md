# Projeto Deadlock Detection

Sistema para simulação e detecção de deadlocks em ambientes de Sistemas Operacionais, desenvolvido como parte de disciplina de Sistemas Operacionais.

## Índice
- [Descrição](#descrição)
- [Funcionalidades](#funcionalidades)
- [Instalação](#instalação)
- [Como Usar](#como-usar)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Equipe](#equipe)
- [Licença](#licença)

## Descrição
Este projeto tem como objetivo simular o gerenciamento de recursos e processos em um sistema operacional, permitindo a análise e detecção de situações de deadlock. A interface gráfica facilita a configuração dos recursos, processos e a visualização do estado do sistema.

## Funcionalidades
- Cadastro de recursos e processos
- Alocação e liberação de recursos
- Eliminação de processos
- Detecção automática de deadlocks
- Visualização do estado dos recursos e processos
- Interface gráfica intuitiva

## Instalação
1. Clone o repositório:
   ```bash
   git clone https://github.com/HielSaraiva/project-deadlock-detection.git
   ```
2. Acesse a pasta do projeto:
   ```bash
   cd project-deadlock-detection
   ```
3. Compile o projeto usando Maven:
   ```bash
   ./mvnw clean install
   ```

## Como Usar
1. Execute a aplicação:
   ```bash
   ./mvnw javafx:run
   ```
2. Utilize a interface gráfica para cadastrar recursos, processos e simular o funcionamento do sistema operacional.

## Tecnologias Utilizadas
- Java 17
- JavaFX 20
- Maven 3.9+

## Equipe
- Hiel Saraiva
- Roberta Alanis
- Charles Lima

## Licença
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.
