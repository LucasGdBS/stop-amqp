import json
from random import choice
from typing import Dict


class Adedonha:
    def __init__(self):
        self.data = self.__carregar_palavras()

    def __carregar_palavras(self):
        try:
            with open('data.json', 'r', encoding='utf-8') as file:
                return json.load(file)
        except FileNotFoundError:
            print('Arquivo não encontrado')
            return {'paises': [], 'frutas': [], 'cores': []}
        except json.JSONDecodeError:
            print('Erro ao decodificar JSON')
            return {'paises': [], 'frutas': [], 'cores': []}

    def sort_letter(self):
        return choice('abcdefghijklmnopqrstuvwxyz').upper()

    def validar_palavra(self, letter:str, word:str) -> bool:
        if not word:
            return False
        return word[0].upper() == letter

    def validar_resposta(self, letter:str, response:Dict[str, str]) -> int:
        pontos = 0
        pais = response.get("pais").capitalize()
        fruta = response.get("fruta").capitalize()
        cor = response.get("cor").capitalize()

        if pais and self.validar_palavra(letter, pais) and pais in self.data["paises"]:
            pontos += 10
        if fruta and self.validar_palavra(letter, fruta) and fruta in self.data["frutas"]:
            pontos += 10
        if cor and self.validar_palavra(letter, cor) and cor in self.data["cores"]:
            pontos += 10

        return pontos
