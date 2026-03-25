# ExpenseApp - Modern Android Finance Tracker
🇺🇸 [English](#-english) | 🇧🇷/🇵🇹 [Português](#-português)

---

## 🇺🇸 English

![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room_Database-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/Dagger_Hilt-000000?style=for-the-badge&logo=dagger&logoColor=white)

**ExpenseApp** is a native Android application focused on personal finance control. Developed with **Modern Android Development (MAD)** best practices, the project demonstrates advanced use of Jetpack Compose, Clean Architecture, and reactivity with Kotlin Flows.

> **Project Goal:** Demonstrate mastery in native Android development focused on scalability, testability, and separation of concerns, aiming for Mid-Level (Pleno) positions.

### 📱 Screenshots & Demo

<div align="center">
  <img src="assents/Home(dark).png" width="180"/>
  <img src="assents/Estatística(dark).png" width="180"/>
  <img src="assents/Histórico%20(dark).png" width="180"/>
  <img src="assents/Nova-transacao-despesa(Dark).png" width="180"/>
</div>

### ✨ Key Features (Highlights)

More than just a finance tracker, this project focuses on technical excellence and User Experience (UX):

- **Custom Canvas Graphics:** A high-performance **Donut Chart** built from scratch using the Compose Canvas API (`drawArc`), avoiding heavy third-party libraries and demonstrating mathematical logic in UI.
- **Reactive Single Source of Truth:** The UI observes a `Flow` from the Room Database. Any change in the data layer is instantly reflected across all screens (Home, History, Statistics) without manual refreshes.
- **Clean Architecture:** Strict separation into **Domain**, **Data**, and **Presentation** layers. The business logic is pure Kotlin, making it independent of the Android framework and highly testable.
- **Testing & Reliability:** Robust unit testing suite implemented for **DAOs** and **Repositories** using **MockK**, **Robolectric**, and **JUnit 4**, ensuring data integrity and logic correctness.
- **Modern UI/UX:** Built entirely with **Material Design 3**, featuring dynamic theming (Dark/Light Mode), intuitive navigation, and smooth animations.

### 🛠️ Tech Stack & Architecture

- **UI:** Jetpack Compose, Material Design 3, Compose Navigation.
- **Architecture:** MVVM (Model-View-ViewModel), Clean Architecture, UDF (Unidirectional Data Flow).
- **Dependency Injection:** Dagger Hilt.
- **Local Storage:** Room Database.
- **Concurrency & Reactive:** Kotlin Coroutines & StateFlow/Flow.
- **Testing:** MockK, Robolectric, JUnit 4, Coroutines Test.

### ⚙️ How to run the project

1. Clone the repository:
   ```bash
   git clone https://github.com/IagoRochaDev/ExpenseApp-Android
   ```
2. Open the project in Android Studio (Iguana or newer recommended).
3. Wait for Gradle to sync dependencies.
4. Run the app on an Emulator or Physical Device.

### 🚀 Roadmap (Next Steps)

- [x] **Unit Testing:** Robust suite for DAOs and Repositories using MockK and Robolectric.
- [x] **CI/CD:** GitHub Actions configured for automated builds and testing on every Push/PR.
- [ ] **Data Export:** Add functionality to export transactions to CSV/PDF.
- [ ] **Unit Testing (UI):** Add Jetpack Compose UI tests.

---

## 🇧🇷/🇵🇹 Português

O **ExpenseApp** é um aplicativo Android nativo focado em controle de finanças pessoais. Desenvolvido com as melhores práticas de **Modern Android Development (MAD)**, o projeto demonstra o uso avançado de Jetpack Compose, Arquitetura Limpa (Clean Architecture) e reatividade com Kotlin Flows.

### ✨ Funcionalidades Principais (Highlights)

Mais do que um simples rastreador de despesas, este projeto foca em excelência técnica e Experiência do Usuário (UX):

- **Gráficos Customizados (Canvas):** Um **Donut Chart** de alto desempenho construído do zero usando a API de Canvas do Compose (`drawArc`), evitando bibliotecas externas pesadas e demonstrando lógica matemática na UI.
- **Fonte de Dados Reativa:** A interface observa um `Flow` do banco de dados Room. Qualquer alteração na camada de dados é refletida instantaneamente em todas as telas (Home, Histórico, Estatísticas).
- **Arquitetura Limpa:** Separação rigorosa entre as camadas de **Domínio**, **Dados** e **Apresentação**. A lógica de negócio é Kotlin puro, tornando-a independente do framework Android e altamente testável.
- **Testes e Confiabilidade:** Suíte de testes unitários implementada para **DAOs** e **Repositórios** utilizando **MockK**, **Robolectric** e **JUnit 4**, garantindo a integridade dos dados.
- **UI/UX Moderna:** Construído inteiramente com **Material Design 3**, apresentando suporte a temas dinâmicos (Modo Escuro/Claro), navegação intuitiva e animações suaves.

### 🛠️ Stack Tecnológico e Arquitetura

- **UI:** Jetpack Compose, Material Design 3, Compose Navigation.
- **Arquitetura:** MVVM, Clean Architecture, UDF (Fluxo Unidirecional de Dados).
- **Injeção de Dependência:** Dagger Hilt.
- **Armazenamento Local:** Room Database.
- **Assincronismo & Reatividade:** Kotlin Coroutines & Flow.
- **Testes:** MockK, Robolectric, JUnit 4, Coroutines Test.

### ⚙️ Como executar o projeto

1. Faça o clone do repositório.
```bash
   git clone https://github.com/IagoRochaDev/ExpenseApp-Android
   ```
2. Abra no Android Studio.
3. Espere o Gradle para sincronizar as dependências.
4. Execute o app em um Emulator ou Aparelho Físico.

### 🚀 Próximos Passos (Roadmap)

- [x] **Testes Unitários:** Suíte robusta para DAOs e Repositórios usando MockK e Robolectric.
- [x] **CI/CD:** Configurado GitHub Actions para automação de testes e builds em cada Push/PR.
- [ ] **Exportação de Dados:** Adicionar funcionalidade para exportar transações para CSV/PDF.
- [ ] **Testes de UI:** Adicionar testes de interface com Jetpack Compose Testing.

---

## 🧑‍💻 Author / Autor
**Developed by / Desenvolvido por Iago Rocha Oliveira**

- **LinkedIn:** [iagorochadev](https://www.linkedin.com/in/iagorochadev/)
- **Portfolio:** [IagoRochaDev](https://github.com/IagoRochaDev/)
- **Email:** iagor.oliveira00@gmail.com
