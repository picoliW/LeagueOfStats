# LeagueOfStats

**LeagueOfStats** é um aplicativo Android desenvolvido em Kotlin que permite aos jogadores de *League of Legends* acessar estatísticas detalhadas dos campeões. Com uma interface amigável e intuitiva!

## Tabela de Conteúdo

- [Funcionalidades](#funcionalidades)
- [Capturas de Tela](#capturas-de-tela)
- [Instalação](#instalação)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Estrutura do Projeto](#Estrutura-do-Projeto)
- [Licença](#licença)

# Funcionalidades

- 🎮 **Estatísticas dos Campeões**: Acesse informações detalhadas sobre cada campeão, incluindo builds recomendadas, feitiços recomendados, tier list de campeões, e muito mais.
- 🔍 **Filtro e Busca**: Encontre rapidamente qualquer campeão usando o sistema de busca.
- 📊 **Análises**: Veja dados apresentados de forma visual facilitam a compreensão das estatísticas.

# Capturas de Tela

| Home | Navegar Por Campeões |
|:----:|:--------------------:|
| ![Home](https://i.imgur.com/pA03BDZ.png) | ![Navegar Por Campeões](https://i.imgur.com/sZUTnTR.png) |

| Tier List dos Campeões | Estatísticas dos Campeões |
|:----------------------:|:-------------------------:|
| ![Tier List dos Campeões](https://i.imgur.com/aLWTsuC.png) | ![Estatísticas dos Campeões](https://i.imgur.com/XxSqOmi.png) |

# Instalação

1. Clone este repositório:
   ```bash
   git clone https://github.com/picoliw/LeagueOfStats.git

2. Abra o projeto no Android Studio

3. Compile e execute o aplicativo no seu dispositivo ou emulador.

# Tecnologias Utilizadas

• Kotlin: Linguagem principal do app.

• HttpURLConnection: Para fazer chamadas à API e buscar dados de campeões.

• Jetpack Compose - Para criar interfaces de usuário de maneira declarativa.

• Android Intents - Para navegação entre atividades.

• Bitmap e BitmapFactory - Para carregar e manipular imagens.

# Estrutura do Projeto
```
LeagueOfStats/
├── main/
│   ├── java/
│   │   ├── database/
│   │   │   ├── ChampionDao                 // Define métodos para acessar os dados do banco de dados
│   │   │   ├── ChampionDatabase            // Gerencia a criação e conexão com o banco de dados de campeões
│   │   │   └── ChampionStatsEntity         // Entidade que representa as estatísticas dos campeões no banco de dados
│   │   ├── models/
│   │   │   └── ChampionModel               // Modelagem dos dados dos campeões
│   │   ├── ui/
│   │   │   ├── activities/
│   │   │   │   ├── ChampionActivity        // Exibe detalhes do campeão selecionado
│   │   │   │   ├── HomeActivity            // Tela inicial do aplicativo
│   │   │   │   ├── MainActivity            // Atividade principal que exibe os campeões
│   │   │   │   ├── RandomChampionsActivity // Exibe 10 campeões aleatórios
│   │   │   │   ├── SplashScreen            // Tela de splash inicial
│   │   │   │   └── TierListActivity        // Exibe a lista de campeões organizados por tier
│   │   │   ├── components/
│   │   │   │   ├── ChampionCard            // Componente visual que exibe uma carta de campeão
│   │   │   │   ├── DisplayImage            // Componente para carregar e exibir imagens
│   │   │   │   ├── NotificationButton      // Botão de notificação configurável
│   │   │   │   ├── PlaySound               // Componente para tocar falas dos personagens
│   │   │   │   ├── SearchBar               // Barra de pesquisa para buscar campeões
│   │   │   │   └── ShareChampion           // Componente para compartilhar informações sobre campeões
│   │   ├── viewModel/
│   │   │   └── NotificationViewModel       // Gerencia os dados de notificações no app
├── README.md                               // Documentação do projeto
```

# Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](./LICENSE) para mais detalhes.
