## Mala (Módulo de Autoempréstimo de Livros do Acervo) - UFRN

### Descrição
O MALA é um aplicativo para que o aluno possa realizar o empréstimo já quando estiver com o livro em mãos, utilizando seu login no SIGAA e senha do SISBI, no local do acervo ou em qualquer dependência da biblioteca, eliminando toda a parte de identificação do usuário e empréstimo do material. Restando apenas a conferência do material na saída da biblioteca, como já é realizado hoje.

A finalidade do projeto é a sua integração ao regulamento do SISBI, implantação e teste na Biblioteca Central, caso seja concretizado como ferramenta do SIGAA ou mesmo ter suas funcionalidades inseridas no aplicativo da BCZM, sendo posteriormente expandido para as bibliotecas setoriais. Em paralelo, o aplicativo será utilizado como projeto avaliativo do grupo para a disciplina DIM0524 - Desenvolvimento de Sistemas para Aplicativos Móveis, ministrada no semestre 2017.2 no Instituto Metrópole Digital/UFRN.

### Integração com a API da UFRN
Como a autenticação na [API da UFRN](https://api.ufrn.br/ "API UFRN") é feita baseada no OAuth2, foi utilizado o fluxo de autorização *Authorization Code*.

### Funcionalidades
* Realizar login no SIGAA;
* Identificar se o usuário tem cadastro no SISBI;
* Possibilitar o cadastro de senha para o uso da biblioteca;
* Possibilitar a alteração da senha da biblioteca;
* Identificar os Livros emprestados pelo o usuário;
* Renovar o empréstimo;
* Fornecer informações sobre os limites de empréstimos;
* Realizar quitação de vínculo com a biblioteca e baixar comprovante em pdf;
* Gerar notificações relacionadas à data de devolução do empréstimo;
* Informar sobre as modalidades de empréstimo ao usuário (Regular, especial e fotocópia);
* Ler código de barras do livro para o empréstimo, com opção de digitação do código para casos de câmera com baixa resolução ou recurso indisponível;
* Auxiliar o usuário para realização do empréstimo, por exemplo: se o usuário já estiver com o número de limite de empréstimos normais sugerir o empréstimo especial (1 dia);
* Empréstimo com confirmação de senha;
* Geração de um comprovante de empréstimo para validação eletrônica (entrada/saída de material);
	
### Autores:
* Joel Felipe - joelfelipe07@gmail.com
* Hugo Oliveira - hugotholiveira@hotmail.com
* Paulo Lopes - paulo.hq.lopes@gmail.com

### Disponível em: 
https://github.com/mala-ufrn/mala

### License

```
Copyright 2017 MALA-UFRN, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
