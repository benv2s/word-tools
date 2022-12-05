def main():
    words = []

    all_words = open("words-58k.txt")
    for word in all_words:
        if 2 <= len(word) - 1 <= 7:
            words.append(word)


    file = open("filteredwords-58k.txt", "w")
    for word in words:
        file.write(word)
    file.close()



if __name__ == "__main__":
    main()