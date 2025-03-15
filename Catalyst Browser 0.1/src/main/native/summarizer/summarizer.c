#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define MAX_SENTENCES 100
#define MAX_SENTENCE_LENGTH 1000

void summarize(const char *text) {
    char sentences[MAX_SENTENCES][MAX_SENTENCE_LENGTH];
    int sentence_count = 0;

    // Split text into sentences
    const char *delimiter = ".";
    char *text_copy = strdup(text);
    char *token = strtok(text_copy, delimiter);
    while (token != NULL && sentence_count < MAX_SENTENCES) {
        strncpy(sentences[sentence_count++], token, MAX_SENTENCE_LENGTH);
        token = strtok(NULL, delimiter);
    }
    free(text_copy);

    // Simple summarization: print the first and last sentence
    if (sentence_count > 0) {
        printf("Summary:\n");
        printf("%s.\n", sentences[0]);
        if (sentence_count > 1) {
            printf("%s.\n", sentences[sentence_count - 1]);
        }
    }
}

int main() {
    const char *text = "This is a simple text. It contains multiple sentences. This is the last sentence.";
    summarize(text);
    return 0;
} 