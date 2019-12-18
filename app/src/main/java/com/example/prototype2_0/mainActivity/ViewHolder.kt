package com.example.prototype2_0.mainActivity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.book_category.view.*
import kotlinx.android.synthetic.main.book_look.view.*
import kotlinx.android.synthetic.main.borrow_look.view.*
import kotlinx.android.synthetic.main.category_btn_look.view.*
import kotlinx.android.synthetic.main.comment_look.view.*
import kotlinx.android.synthetic.main.inner_recycler_view.view.*
import kotlinx.android.synthetic.main.post_look.view.*

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    //posts page
    var post_card_view = view.post_card_view
    var post_more = view.post_more
    var user_name_post = view.user_name_post
    var date_post = view.date_post
    var text_post = view.text_post
    var post_profile_photo = view.post_profile_photo

    //borrow book request
    var borrow_book_card_view=view.borrow_card
    var borrow_book_book_image=view.book_image_borrow_look
    var borrow_book_book_name=view.book_name_borrow_look
    var borrow_book_user_name=view.user_name_borrow_look
    var borrow_book_user_un=view.user_un_borrow_look
    var borrow_book_more=view.more_borrow_book

    //comments page
    var comment_more = view.comment_more
    var comment_card_view = view.comment_card_view
    var user_name_comment = view.user_name_comment
    var text_comment = view.text_comment
    var comment_profile_photo = view.comment_profile_photo

    //catalog book
    var inner_category_name = view.inner_category_name
    var inner_recycler_view = view.inner_recycler_view

    //books
    var book_layout=view.book_layout
    var book_image_book_look=view.book_image_book_look
    var book_name_book_look=view.book_name_book_look
    var book_date_book_look=view.book_date_book_look
    var more_shelves_book=view.more_shelves_book

    //category
    var category_recycler_view = view.category_recycler_view

  //categories btns
    var category_btn = view.category_btn


}