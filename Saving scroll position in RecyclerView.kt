    private var scrollState: Parcelable? = null

    override fun onResume() {
        binding.recyclerView.layoutManager?.onRestoreInstanceState(scrollState)
        super.onResume()
    }

    override fun onPause() {
        scrollState = binding.recyclerView.layoutManager?.onSaveInstanceState()
        super.onPause()
    }
