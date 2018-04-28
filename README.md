Trabalho da disciplina de Sistemas Distribuidos do curso de <a href="https://www.feevale.br/graduacao/sistemas-para-internet" target="_blank">Sistemas para Internet</a> da <a href="https://www.feevale.br" target="_blank">Universidade Feevale</a>.

## Desafio

### Compartilhamento de Arquivos
Desenvolver um sistema de compartilhamento de arquivos centralizado, formado por nodos
assimétricos. O sistema deve implementar os seguintes requisitos fundamentais:

* Manter réplicas idênticas, não fragmentadas, de todos os arquivos em todos os nodos
participantes da rede.

* Permitir que qualquer nodo conecte ou desconecte da rede, a qualquer momento,
mantendo o conteúdo consistente. As conexões acontecem com base em IP e porta,
sendo necessário conhecer o endereço do nó central, não sendo necessário criar
serviços de nomeação.

* Replicar todo o conteúdo para todo e qualquer nodo que venha a fazer parte da rede.

* Replicar para todos os nodos da rede inclusões de novos arquivos e exclusões de
arquivos.

Assumir como diretório para armazenamento de arquivos o seguinte caminho: C:\filesharefolder
Não é necessário compartilhar subdiretórios.
Não é necessário desenvolver interfaces gráficas.
