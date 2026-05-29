# 💳 V-Wallet — Gerenciador de Cartões Digitais

Aplicativo Android nativo para gerenciamento de cartões digitais, compras e configurações de conta. Desenvolvido com **Kotlin**, **Jetpack Compose** e **Room (SQLite)**, seguindo a arquitetura **MVVM** com padrão Repository.

---

## 📱 Funcionalidades

### Autenticação
- **Login** — autenticação via API REST (Retrofit) com persistência de sessão local
- **Cadastro** — criação de conta com validação (mínimo 8 caracteres na senha)
- **Esqueci a senha** — fluxo de recuperação com envio de código
- **Redefinir senha** — alteração de senha com validação

### Cartões
- **Gerenciamento completo** — criar, bloquear, excluir e favoritar cartões
- **Cartão temporário (24h)** — cartões com validade de 24 horas que expiram automaticamente
- **Limite diurno e noturno** — configuração de limites independentes via slider
- **Visualização de dados** — número completo, validade e CVV com botão mostrar/ocultar
- **Filtro de favoritos** — botão estrela na Home filtra apenas cartões favoritos
- **Pager horizontal** — navegação entre cartões com setas e indicador de página

### Estados Visuais dos Cartões
| Estado | Visual |
|--------|--------|
| Ativo | Gradiente colorido normal |
| Bloqueado (desativado) | Cinza + badge vermelho "BLOQUEADO" |
| Temporário ativo | Colorido + badge dourado "24H / TEMP" |
| Temporário expirado | Cinza + badge vermelho "EXPIRADO" + overlay "CARTÃO CANCELADO" |

### Compras
- **Geração automática** — simulador gera compras a cada 1-5 segundos para cartões ativos e válidos
- **Limite de 15 compras** por cartão (para automaticamente)
- **Datas variadas** — compras são geradas com datas aleatórias dos últimos 30 dias
- **Categorias** — Alimentação, Transporte, Assinatura, Saúde, Compras, Contas, Entretenimento
- **Lista por cartão** — compras filtradas pelo cartão selecionado no pager

### Configurações
- **Tema** — Claro, Escuro ou Sistema (persistido localmente)
- **Editar perfil** — alterar nome de exibição e e-mail
- **Segurança** — redireciona para redefinição de senha
- **Excluir conta** — remoção completa com diálogo de confirmação
- **Logout** — encerra sessão e para o simulador de compras

### Bancos
- **Catálogo de bancos** — seleção visual com cores e bandeiras
- **Criação de cartão personalizada** — vinculado ao banco escolhido

---

## 🏗️ Arquitetura

O projeto segue **MVVM (Model-View-ViewModel)** com padrão **Repository**:

```
┌─────────────────────────────────────────────────────────┐
│                       UI Layer                          │
│  Screens (Compose) → ViewModels (StateFlow/UiState)    │
├─────────────────────────────────────────────────────────┤
│                     Domain Layer                        │
│  Repository Interfaces → Models (data classes)         │
├───────────────────────────────────────────��─────────────┤
│                      Data Layer                         │
│  Room (SQLite) ← DAOs ← Entities                       │
│  Retrofit ← AuthService (API REST)                     │
└─────────────────────────────────────────────────────────┘
```

### Fluxo de dados

```
UI (Composable)
  ↕ coleta StateFlow
ViewModel (expõe UiState<T>)
  ↕ chama suspend / coleta Flow
Repository (interface)
  ↕ implementação Room*
DAO (Room @Query)
  ↕ SQLite
Entity (tabela)
```

---

## 🗂️ Estrutura de Pacotes

```
com.example.wallet/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt              # Banco Room (v8, 5 entidades)
│   │   ├── CardPreferencesManager.kt   # SharedPreferences para dados visuais
│   │   ├── dao/
│   │   │   ├── AccountDao.kt
│   │   │   ├── CardDao.kt
│   │   │   ├── PurchaseDao.kt
│   │   │   ├── BankAccountDao.kt
│   │   │   └── BankConnectionDao.kt
│   │   └── entity/
│   │       ├── AccountEntity.kt
│   │       ├── CardEntity.kt
│   │       ├── PurchaseEntity.kt
│   │       ├── BankAccountEntity.kt
│   │       └── BankConnectionEntity.kt
│   ├── mock/
│   │   └── MockData.kt
│   └── remote/
│       └── AuthService.kt              # Retrofit API
├── model/
│   ├── UserModel.kt
│   ├── CardModel.kt
│   ├── PurchaseModel.kt
│   └── BankAccount.kt
├── navigation/
│   ├── AppNavHost.kt                   # Grafo de navegação
│   └── Routes.kt                       # Rotas (sealed class)
├── repository/
│   ├── UserRepository.kt              # Interface
│   ├── RoomUserRepository.kt          # Implementação Room
│   ├── CardRepository.kt
│   ├── RoomCardRepository.kt
│   ├── PurchaseRepository.kt
│   ├── RoomPurchaseRepository.kt
│   ├── BankRepository.kt
│   ├── RoomBankRepository.kt
│   ├── AuthRepository.kt             # Retrofit
│   ├── FakeUserRepository.kt         # Mock para testes
│   ├── FakeCardRepository.kt
│   └── FakePurchaseRepository.kt
├── state/
│   └── UiState.kt                     # data class genérico (isLoading, data, error)
├── ui/
│   ├── components/
│   │   ├── CardItem.kt                # Componente visual do cartão
│   │   ├── cardmanagement/
│   │   │   ├── CardManagementBottomSheet.kt
│   │   │   ├── CardPreview.kt
│   │   │   ├── SettingsRow.kt
│   │   │   └── LimitSlider.kt
│   │   └── ...
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── SignUpScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   ├── ResetPasswordScreen.kt
│   │   ├── HomeScreen.kt             # Tela principal (pager + compras)
│   │   ├── RequestCardScreen.kt
│   │   ├── PurchasesScreen.kt
│   │   ├── SettingsScreen.kt
│   │   └── EditProfileScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/
│   ├── ServiceLocator.kt             # DI manual (singleton)
│   ├── SessionManager.kt             # Sessão persistida (SharedPreferences)
│   ├── ThemePreferences.kt           # Preferências de tema
│   ├── NetworkModule.kt              # Configuração Retrofit/OkHttp
│   ├── PurchaseSimulator.kt          # Gerador automático de compras
│   └── Formatters.kt                 # Utilitários de formatação
├── viewmodel/
│   ├── HomeViewModel.kt (MyCardsViewModel)
│   ├── LoginViewModel.kt
│   ├── SignUpViewModel.kt
│   ├── ForgotPasswordViewModel.kt
│   ├── EditProfileViewModel.kt
│   ├── CardManagementViewModel.kt
│   ├── RequestCardViewModel.kt (CreateCardViewModel)
│   ├── PurchasesViewModel.kt
│   └── SettingsViewModel.kt
└── MainActivity.kt
```

---

## 🗄️ Banco de Dados (Room SQLite)

### Diagrama de Entidades

```
┌──────────────┐
│   accounts   │ ← Conta/usuário (PK: id autoGenerate)
│──────────────│
│ id (Long)    │
│ name         │
│ email        │
│ balance      │
│ createdAt    │
└──────┬───────┘
       │ 1:N (CASCADE)
       ▼
┌──────────────────┐        ┌────────────────────┐
│      cards       │        │  bank_connections  │
│──────────────────│        │────────────────────│
│ id (Long, PK)    │        │ accountId (FK)     │
│ accountId (FK)   │        │ bankAccountId (FK) │
│ name             │        │ connectedAt        │
│ lastDigits       │        └────────────────────┘
│ limit            │                  │
│ isFavorite       │                  ▼
│ isActive         │        ┌────────────────────┐
│ dayLimit         │        │   bank_accounts    │
│ nightLimit       │        │────────────────────│
│ brand            │        │ id (Long, PK)      │
│ expiry           │        │ bankName           │
│ bankColor        │        │ bankColor          │
│ bankName         │        │ cardBrand          │
│ isTemporary      │        │ cardNetwork        │
│ createdAt        │        └────────────────────┘
└──────┬───────────┘
       │ 1:N (CASCADE)
       ▼
┌──────────────────┐
│    purchases     │
│──────────────────│
│ id (Long, PK)    │
│ accountId (FK)   │
│ cardId (FK)      │
│ title            │
│ amount           │
│ date             │
│ category         │
│ createdAt        │
└──────────────────┘
```

### Regras de Integridade
- **CASCADE DELETE**: Excluir conta → apaga cartões e compras automaticamente
- **CASCADE DELETE**: Excluir cartão → apaga compras vinculadas
- **Isolamento por usuário**: Todas as queries filtram por `accountId` do usuário logado
- **Migração destrutiva** (protótipo): `fallbackToDestructiveMigration(dropAllTables = true)`

---

## 🔧 Tecnologias e Bibliotecas

| Tecnologia | Uso |
|------------|-----|
| **Kotlin 2.0.21** | Linguagem principal |
| **Jetpack Compose** (BOM 2024.09.00) | UI declarativa |
| **Room 2.7.1** | Banco de dados local (SQLite) |
| **Retrofit 2.11.0** | Cliente HTTP para API REST |
| **OkHttp 4.12.0** | Interceptor e logging |
| **Navigation Compose 2.8.4** | Navegação entre telas |
| **Material 3** | Design system |
| **Material Icons Extended** | Ícones vetoriais |
| **Lifecycle ViewModel Compose** | ViewModels com escopo |
| **KSP** (em vez de kapt) | Processamento de anotações Room |
| **kotlinx.serialization** | Serialização JSON (DTOs) |
| **Splash Screen API** | Tela de splash nativa |
| **SharedPreferences** | Sessão, tema e preferências de cartão |
| **StateFlow / Flow** | Reatividade e observação de estado |
| **Coroutines** | Operações assíncronas |

---

## ⚙️ Configuração e Build

### Requisitos
- **Android Studio** Ladybug ou superior
- **JDK 11+**
- **SDK 36** (compilação/target)
- **SDK 24** (mínimo)

### Comandos

```bash
# Build do APK debug
./gradlew assembleDebug

# Executar testes unitários
./gradlew test

# Testes instrumentados (requer emulador/dispositivo)
./gradlew connectedDebugAndroidTest
```

### Gerenciamento de Dependências
Todas as versões centralizadas em `gradle/libs.versions.toml` e referenciadas via aliases `libs.*` no `build.gradle.kts`.

---

## 🔐 Injeção de Dependências

O projeto usa **Service Locator** manual (sem Hilt/Koin):

```kotlin
// utils/ServiceLocator.kt
object ServiceLocator {
    fun init(context: Context) { ... }

    val sessionManager: SessionManager
    val cardRepository: CardRepository      // by lazy { RoomCardRepository(...) }
    val purchaseRepository: PurchaseRepository
    val userRepository: UserRepository
    val bankRepository: BankRepository
    val purchaseSimulator: PurchaseSimulator
}
```

Inicializado no `Application.onCreate()` antes de qualquer acesso. ViewModels usam default parameters injetando de `ServiceLocator`.

---

## 🎮 Simulador de Compras

O `PurchaseSimulator` roda em background enquanto o usuário está logado:

- **Intervalo**: 1-5 segundos (aleatório) entre cada compra
- **Limite**: máximo **15 compras por cartão**
- **Validação dupla**: verifica elegibilidade do cartão no momento de gerar
- **Datas variadas**: cada compra recebe data aleatória dos últimos 30 dias
- **Cartões inelegíveis** (não geram compras):
  - Bloqueados (`isActive = false`)
  - Temporários expirados (>24h desde criação)
  - Com validade vencida (campo `expiry`)

Ciclo de vida:
- **Inicia**: após login ou ao abrir app com sessão válida
- **Para**: ao fazer logout

---

## 🧭 Navegação

Rotas definidas via `sealed class Routes`:

| Rota | Tela | Descrição |
|------|------|-----------|
| `login` | LoginScreen | Autenticação |
| `signup` | SignUpScreen | Criar conta |
| `forgot_password` | ForgotPasswordScreen | Recuperar senha |
| `reset_password` | ResetPasswordScreen | Redefinir senha |
| `my_cards` | HomeScreen | Tela principal (cartões + compras) |
| `settings` | SettingsScreen | Ajustes (tema, segurança, conta) |
| `purchases` | PurchasesScreen | Histórico de compras |
| `edit_profile` | EditProfileScreen | Editar perfil do usuário |

---

## 📊 Gerenciamento de Estado

Todos os ViewModels usam um `UiState<T>` genérico:

```kotlin
data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null
)
```

Exposto via `StateFlow<UiState<T>>` e coletado nos Composables com `collectAsStateWithLifecycle()`.

---

## 👤 Sessão e Multi-Usuário

- **SessionManager** persiste o `userId` em SharedPreferences
- Repositórios filtram dados reativamente via `sessionManager.currentUserId` (StateFlow)
- Trocar de usuário = trocar automaticamente todos os dados exibidos
- Dados persistem entre sessões (login/logout não apaga dados)
- Seed de dados de teste para o usuário `teste123` (1 cartão + 8 compras iniciais)

---

## 🎨 Temas

Suporte a 3 modos de tema (persistido em SharedPreferences):
- ☀️ **Claro** — cores claras
- 🌙 **Escuro** — cores escuras
- 📱 **Sistema** — segue configuração do dispositivo

---

## 📋 Checklist para Nova Feature

1. **Model** → `model/` (data class)
2. **Entity + DAO** → `data/local/entity/` + `data/local/dao/`. Registrar em `AppDatabase`
3. **Repository interface** → `repository/`. Implementação `Room*Repository`
4. **ServiceLocator** → expor via `val` property com `by lazy`
5. **ViewModel** → `viewmodel/`, usar `UiState<T>`, coletar de repository flows
6. **Screen** → `ui/screens/`. Aceitar navigation lambdas, sem NavController direto
7. **Route** → adicionar ao `Routes` sealed class + `AppNavHost`

---

## 📝 Convenções

- **Idioma**: Kotlin — comentários e strings em Português Brasileiro
- **Serialização**: `kotlinx.serialization` (não Gson/Moshi)
- **Processador de anotações**: KSP (não kapt)
- **Catalog de versões**: `gradle/libs.versions.toml`
- **Sem framework DI**: Service Locator manual

---

## 👥 Equipe

- Maria Rita Guedes
- Diego Souza
- Oswaldo Schermach
- Mateus Guerra
- Alan Diego

---

## 📄 Licença

Projeto acadêmico / uso pessoal.

