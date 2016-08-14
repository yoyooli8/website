package com.lufficc.service;

import com.lufficc.model.Article;
import com.lufficc.model.Category;
import com.lufficc.model.Folder;
import com.lufficc.model.form.ArticleForm;
import com.lufficc.model.support.ArticleStatus;
import com.lufficc.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lcc_luffy on 2016/8/8.
 */
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    private final CategoryService categoryService;

    private final FolderService folderService;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, FolderService folderService, CategoryService categoryService) {
        this.articleRepository = articleRepository;
        this.folderService = folderService;
        this.categoryService = categoryService;
    }

    public Page<Article> getPageableArticles(int page, int size, Sort sort) {
        System.out.println("getPageableArticles");
        PageRequest pageRequest = new PageRequest(
                page,
                size > 10 ? 10 : size,
                sort
        );
        return articleRepository.findAllPublishedArticles(pageRequest);
    }

    public Article create(ArticleForm articleForm, String md) {
        Article article = generateArticle(null, articleForm);
        article.setMarkdown(md);
        save(article);
        return article;
    }

    public Article update(Long oldArticleId, ArticleForm articleForm, String md_content) {
        Article oldArticle = findOne(oldArticleId);
        generateArticle(oldArticle, articleForm);
        oldArticle.setMarkdown(md_content);
        return save(oldArticle);
    }

    private Article generateArticle(Article article, ArticleForm articleForm) {
        Category category = categoryService.findByName(articleForm.getCategory());
        if (article == null)
            article = new Article(
                    articleForm.getTitle(),
                    articleForm.getDescription(),
                    articleForm.getAuthor(),
                    articleForm.getOriginUrl()
            );
        else {
            article.setTitle(articleForm.getTitle());
            article.setDescription(articleForm.getDescription());
            article.setAuthor(articleForm.getAuthor());
            article.setOriginUrl(articleForm.getOriginUrl());
        }
        article.setCategory(category);
        article.setArticleStatus(
                ArticleStatus.DRAFT.toString().equals(
                        articleForm.getArticleStatus()) ?
                        ArticleStatus.DRAFT : ArticleStatus.PUBLISHED
        );
        if (articleForm.getFolder() != -1) {
            Folder folder = folderService.findOne(articleForm.getFolder());
            article.setFolder(folder);
            article.setCategory(folder.getCategory());
        } else {
            article.setFolder(null);
        }
        return article;
    }

    public void delete(Long id) {
        articleRepository.delete(id);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public Article findOne(Long id) {
        return articleRepository.findOne(id);
    }

    public Article save(Article article) {
        return articleRepository.save(article);
    }

    public long count() {
        return articleRepository.count();
    }

    public long countPublished() {
        return articleRepository.countPublished();
    }

    public long countDraft() {
        return articleRepository.countDraft();
    }
}
